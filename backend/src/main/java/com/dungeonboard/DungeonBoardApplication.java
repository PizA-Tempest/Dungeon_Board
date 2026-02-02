package com.dungeonboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DungeonBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DungeonBoardApplication.class, args);
    }
}
