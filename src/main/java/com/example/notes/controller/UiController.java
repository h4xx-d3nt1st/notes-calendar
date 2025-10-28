package com.example.notes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

  // корень и оба варианта /ui /ui/
  @GetMapping({"/", "/ui", "/ui/"})
  public String index() {
    // важно: FORWARD, а не redirect — чтобы не было 302 и циклов
    return "forward:/ui/index.html";
  }
}
