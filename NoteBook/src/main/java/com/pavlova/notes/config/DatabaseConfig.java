package com.pavlova.notes.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.pavlova.notes.database")
public class DatabaseConfig {
}
