package com.orbvpn.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  @GetMapping("/login")
  public String viewLoginPage() {
    System.out.println("LoginController viewLoginPage");
    // custom logic before showing login page...
    return "login";
  }
}
