package com.pavlova.notes.config;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.pavlova.notes.utils")
@Import({DatabaseConfig.class, WebConfig.class})
public class MainConfig {
}
