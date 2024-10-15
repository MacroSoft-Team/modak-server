package com.macrosoft.modakserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                new Server().url("http://localhost:8080").description("로컬 테스트 서버 (HTTP)"),
                new Server().url(devServerUrl).description("원격 개발 서버 (HTTPS)"));


        String JWT = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);
        Components components = new Components().addSecuritySchemes(JWT, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(JWT)
                .description("Access Token 을 입력하세요")
        );



        return new OpenAPI()
                .servers(servers)
                .info(info)
                .components(components)
                .security(List.of(securityRequirement));
    }
}