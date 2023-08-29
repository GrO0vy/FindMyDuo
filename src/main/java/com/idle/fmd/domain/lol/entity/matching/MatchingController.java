package com.idle.fmd.domain.lol.entity.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class MatchingController {
    private final MatchingService matchingService;

    @MessageMapping("/start")
    public void startMatching(){
        matchingService.startMatching();
    }
}
