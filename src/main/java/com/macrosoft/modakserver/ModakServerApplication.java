package com.macrosoft.modakserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ModakServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModakServerApplication.class, args);
    }

}
