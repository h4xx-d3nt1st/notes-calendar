package com.example.notes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping({"/", ""})
  public String root() {
    // редирект на папку ui, где лежит index.html
    return "forward:/ui/index.html";
  }

  // на всякий случай — если зайдут на /ui без слэша
  @GetMapping("/ui")
  public String uiNoSlash() {
    return "forward:/ui/index.html";
  }
}
