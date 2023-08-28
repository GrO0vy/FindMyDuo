package com.idle.fmd.domain.match;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchController {
    @GetMapping("/main")
    public String main(){
        return "main";
    }

    @GetMapping("/login")
    public String loginForm(){return "login";}
}
