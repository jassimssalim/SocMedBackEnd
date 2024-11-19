package SocMedApp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // Marks this class as a configuration class for Spring
public class CorsConfig implements WebMvcConfigurer {

    // Override the addCorsMappings method to configure CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow cross-origin requests from localhost:3000
        registry.addMapping("/**")  // Apply to all endpoints
                .allowedOrigins("http://localhost:3000")  // Frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE");  // Allowed HTTP methods
    }
}
