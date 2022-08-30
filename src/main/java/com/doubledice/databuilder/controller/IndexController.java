package com.doubledice.databuilder.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ponomarev 27.07.2022
 */
@Controller
class IndexController {

    @GetMapping
    public String index(Authentication authentication) {
        return "index";
    }
    @GetMapping("/auth")
    public Authentication getAuth(Authentication authentication) {
        return authentication;
    }

}
