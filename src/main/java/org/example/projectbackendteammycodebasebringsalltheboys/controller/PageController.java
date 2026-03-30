package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/dashboard")
  public String dashboard() {
    return "dashboard";
  }

  @GetMapping("/admin")
  public String adminPage() {
    return "admin";
  }
}
