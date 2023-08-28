package com.idle.fmd.domain.match;

import com.google.gson.Gson;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.service.CustomUserDetailsManager;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import com.idle.fmd.global.common.utils.TierConverter;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SimpleChatHandler extends TextWebSocketHandler {
    private final CustomUserDetailsManager manager;
    private final Gson gson = new Gson();
    private final JwtTokenUtils jwtTokenUtils;
    private final TierConverter tierConverter = new TierConverter();

    public SimpleChatHandler(CustomUserDetailsManager manager, JwtTokenUtils jwtTokenUtils){
        this.manager = manager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String requestParameters = session.getUri().getQuery();
        String[] paramArray = requestParameters.split("&");
        for(int i = 0; i < 4; i++){
            String param = paramArray[i];
            paramArray[i] = param.substring(param.indexOf("=") + 1);
        }

        String token = paramArray[0];
        String mode = paramArray[1];
        String myLine = paramArray[2];
        String duoLine = paramArray[3];

        log.info(token);

        String accountID = tokenToAccountId(token);
        UserEntity userEntity = manager.loadUserEntityByAccountId(accountID);

        Map<String, Object> attributes = session.getAttributes();
        attributes.put("nickname", userEntity.getNickname());
        attributes.put("lolNickname", userEntity.getLolAccount().getNickname());
        attributes.put("tier", userEntity.getLolAccount().getTier());
        attributes.put("mode", mode); // 프론트에서 선택한 모드로 바꿔야함
        attributes.put("myLine", myLine); // 프론트에서 선택한 라인으로 바꿔야함
        attributes.put("duoLine", duoLine); // 프론트에서 선택한 상대 라인으로 바꿔야함

//        Map<String, WebSocketSession> newSession = new HashMap<>();
//        newSession.put(session.getId(), session);
        sessions.add(session);

//        for(Map<String, WebSocketSession> connectedMap: sessions){
        for(WebSocketSession connected: sessions){
//            String connectedId = connectedMap.keySet().toArray()[0].toString();
//            WebSocketSession connected = connectedMap.get(connectedId);

//            if(connectedId == session.getId()) continue;

            if(
                    connected.getAttributes().get("mode").equals(session.getAttributes().get("mode")) &&
                    connected.getAttributes().get("duoLine").equals(session.getAttributes().get("myLine")) &&
                    connected.getAttributes().get("myLine").equals(session.getAttributes().get("duoLine"))
            ){
                boolean tierInRange = false;
                String myTier = session.getAttributes().get("tier").toString();
                String duoTier = connected.getAttributes().get("tier").toString();

                log.info(myTier);
                log.info(duoTier);
                log.info(mode);

                if(mode.equals("solo")) tierInRange = tierConverter.soloTierInRange(myTier, duoTier);
                if(mode.equals("flex")) tierInRange = tierConverter.flexTierInRange(myTier, duoTier);

                log.info("{}", tierInRange);

                if(tierInRange){
                    MatchResponseDto matchResponseDto = new MatchResponseDto();
                    matchResponseDto.setMode(connected.getAttributes().get("mode").toString());
                    matchResponseDto.setNickname(connected.getAttributes().get("nickname").toString());
                    matchResponseDto.setTier(connected.getAttributes().get("tier").toString());
                    matchResponseDto.setMyLine(connected.getAttributes().get("myLine").toString());
                    //matchResponseDto.setOpponent(connected.getAttributes().get("opponent").toString());

                    // 커넥티드의 목적지를 세션으로 설정
                    connected.getAttributes().put("destination", session.getId());

                    // 세션으로 커넥티드의 정보를 전송
                    String json = gson.toJson(matchResponseDto);
                    TextMessage textMessage = new TextMessage(json);
                    session.sendMessage(textMessage);


                    MatchResponseDto matchResponseDto2 = new MatchResponseDto();
                    matchResponseDto2.setMode(session.getAttributes().get("mode").toString());
                    matchResponseDto2.setNickname(session.getAttributes().get("nickname").toString());
                    matchResponseDto2.setTier(session.getAttributes().get("tier").toString());
                    matchResponseDto2.setMyLine(session.getAttributes().get("myLine").toString());
                    //matchResponseDto2.setOpponent(session.getAttributes().get("opponent").toString());

                    // 세션의 목적지를 커넥티드로 설정
                    session.getAttributes().put("destination", connected.getId());

                    // 커넥티드에 세션의 정보를 보냄
                    String json2 = gson.toJson(matchResponseDto2);
                    TextMessage textMessage2 = new TextMessage(json2);
                    connected.sendMessage(textMessage2);
                }
            }
        }

        log.info("connected with session id {}, total sessions: {}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("received: {}", payload);

        session.getAttributes().put("answer", message.getPayload());

        WebSocketSession destination = null;
        for(WebSocketSession connected: sessions){
            if(session.getAttributes().get("destination").equals(connected.getId().toString())) {
                destination = connected;
                break;
            }
        }

        String myAnswer = session.getAttributes().get("answer").toString().toString();
        String destinationAnswer = String.valueOf(destination.getAttributes().get("answer"));
        log.info("my: {}, des: {}",myAnswer,destinationAnswer);

        if(destinationAnswer.equals("null")) return;
        else if(myAnswer.equals("accept") && destinationAnswer.equals("accept")){
            TextMessage textMessage = new TextMessage("매칭완료");
            session.sendMessage(textMessage);
            destination.sendMessage(textMessage);
        }
        else{
            TextMessage textMessage = new TextMessage("매칭실패");
            session.sendMessage(textMessage);
            destination.sendMessage(textMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection with {} closed, total sessions: {}", session.getId(), sessions.size());
        sessions.remove(session);
    }

    public String tokenToAccountId(String token){
        if(jwtTokenUtils.validate(token)){
            Claims claims = jwtTokenUtils.parseClaims(token);
            String accountId = claims.getSubject().toString();
            return accountId;
        }
        return null;
    }
}
