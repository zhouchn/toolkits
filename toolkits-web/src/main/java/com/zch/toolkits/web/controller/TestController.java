package com.zch.toolkits.web.controller;

import com.zch.toolkits.web.api.version.ApiVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @ApiVersion("1.0")
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
