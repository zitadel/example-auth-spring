package com.zitadel.demo.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@SuppressFBWarnings("SPRING_ENDPOINT")
public class AuthController {

    @GetMapping("/signin")
    public String signin(@RequestParam(required = false) String error,
                         @RequestParam(required = false) String callbackUrl,
                         Model model) {

        Map<String, String> zitadelProvider = new HashMap<>();
        zitadelProvider.put("id", "zitadel");
        zitadelProvider.put("name", "ZITADEL");
        zitadelProvider.put("signinUrl", "/oauth2/authorization/zitadel");

        model.addAttribute("providers", Collections.singletonList(zitadelProvider));
        model.addAttribute("callbackUrl", callbackUrl != null ? callbackUrl : "/profile");

        if (error != null) {
            model.addAttribute("message", getErrorMessage(error, "signin-error"));
        }

        return "auth/signin";
    }

    @GetMapping("/error")
    public String errorPage(@RequestParam(required = false) String error, Model model) {
        Map<String, String> msg = getErrorMessage(error, "auth-error");
        model.addAttribute("heading", msg.get("heading"));
        model.addAttribute("message", msg.get("message"));
        return "auth/error";
    }

    @GetMapping("/logout/callback")
    public String logoutSuccess() {
        return "auth/logout/success";
    }

    @GetMapping("/csrf")
    @ResponseBody
    public Map<String, String> csrf(CsrfToken token) {
        if (token != null) {
            return Collections.singletonMap("csrfToken", token.getToken());
        }
        return Collections.emptyMap();
    }

    private Map<String, String> getErrorMessage(String errorCode, String category) {
        Map<String, String> result = new HashMap<>();
        if (errorCode == null) errorCode = "default";

        if ("signin-error".equals(category)) {
            if (errorCode.equals("oauthaccountnotlinked")) {
                result.put("heading", "Account Not Linked");
                result.put("message", "To confirm your identity, sign in with the same account you used originally.");
            } else {
                result.put("heading", "Sign-in Failed");
                result.put("message", "Try signing in with a different account.");
            }
        } else {
            if (errorCode.equals("accessdenied")) {
                result.put("heading", "Access Denied");
                result.put("message", "You do not have permission to sign in.");
            } else {
                result.put("heading", "Authentication Error");
                result.put("message", "An unexpected error occurred. Please try again.");
            }
        }
        return result;
    }
}
