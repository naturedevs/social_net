package com.orbvpn.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
  @GetMapping("/welcome")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("Hell OAuth2");
  }
  @GetMapping("/oauth2/1")
  public ResponseEntity<String> sayHello1() {
    return ResponseEntity.ok("Hell 1");
  }
  @GetMapping("/oauth2/2")
  public ResponseEntity<String> sayHello2() {
    return ResponseEntity.ok("Hell 2");
  }
}
