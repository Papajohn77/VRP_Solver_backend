package tech.johnpapadatos.vrpsolverapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("VRP Solver API")
            .version("0.1.0");

        return new OpenAPI().info(info);
    }
}
