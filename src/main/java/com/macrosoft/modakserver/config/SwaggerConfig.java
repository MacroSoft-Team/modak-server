package com.macrosoft.modakserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.dev.url}")
    private String devServerUrl;


    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("Modak Server API")
                .version("1.0.0")
                .description("Modak 서버의 API 문서입니다.");

        List<Server> servers = List.of(
                new Server().url("http://localhost:8080").description("Local development server (HTTP)"),
                new Server().url(devServerUrl).description("Remote Development server (HTTPS)"));

        return new OpenAPI()
                .servers(servers)
                .info(info);
    }
}