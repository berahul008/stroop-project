package com.example.stroop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping({"/", "/index"})
    public String index() {
        // Because we moved index.html to /static, forward to it:
        return "forward:/index.html";
    }
}
