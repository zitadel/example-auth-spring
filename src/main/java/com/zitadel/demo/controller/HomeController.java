package com.zitadel.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressFBWarnings("SPRING_ENDPOINT")
public class HomeController {

    private final ObjectMapper objectMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public HomeController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser principal, Model model) {
        boolean isAuthenticated = (principal != null);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("loginUrl", "/oauth2/authorization/zitadel");

        return "index";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser principal, Model model) throws JsonProcessingException {
        if (principal == null) {
            return "redirect:/auth/signin";
        }

        String userJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(principal.getClaims());
        model.addAttribute("userJson", userJson);

        return "profile";
    }
}
