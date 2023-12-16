package tech.johnpapadatos.vrpsolverapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(
        @Value("${server.url}") String serverUrl
    ) {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Server URL");

        Info info = new Info()
            .title("VRP Solver API")
            .version("0.1.0");

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
