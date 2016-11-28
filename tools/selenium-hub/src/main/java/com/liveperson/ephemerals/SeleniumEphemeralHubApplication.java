package com.liveperson.ephemerals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by waseemh on 11/24/16.
 */

@SpringBootApplication
public class SeleniumEphemeralHubApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SeleniumEphemeralHubApplication.class, args);
    }
}
