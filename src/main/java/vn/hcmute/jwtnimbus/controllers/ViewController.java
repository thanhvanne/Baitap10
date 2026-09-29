package vn.hcmute.jwtnimbus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ViewController {

    @GetMapping
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("user/profile")
    public String profilePage() {
        return "profile";
    }
}