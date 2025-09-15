package com.driveai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DriveAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriveAiAgentApplication.class, args);
    }
}
