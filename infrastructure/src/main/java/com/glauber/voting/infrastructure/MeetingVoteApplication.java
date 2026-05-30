package com.glauber.voting.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.glauber.voting")
public class MeetingVoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingVoteApplication.class, args);
    }

}
