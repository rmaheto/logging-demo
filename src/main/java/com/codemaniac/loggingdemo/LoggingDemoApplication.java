package com.codemaniac.loggingdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@Slf4j
public class LoggingDemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        log.info("logger is working");
        SpringApplication.run(LoggingDemoApplication.class, args);
    }

}
