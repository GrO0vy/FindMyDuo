package com.idle.fmd.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final SimpleChatHandler simpleChatHandler;

    @Override
    // 웹 소켓 핸들러 객체를 등록하기 위한 메서드
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ws/chat 이  chat.html 의 const webSocket = new WebSocket("ws://localhost:8080/ws/chat") 와 연결됨
        registry.addHandler(simpleChatHandler, "ws/chat")
                .setAllowedOrigins("*");
    }


}
