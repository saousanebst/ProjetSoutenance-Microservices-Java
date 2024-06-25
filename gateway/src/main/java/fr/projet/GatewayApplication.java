package fr.projet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	
    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Configuration pour aller vers produit service
            .route(r ->
                r   .path("/api/compte/**")
                    .uri("lb://service-compte")
            )
            
            // Configuration pour aller vers commentaire service
            .route(r ->
                r   .path("/api/utilisateur/**")
                    .uri("lb://service-utilisateur")
            )



			// Configuration pour aller vers commentaire service
            .route(r ->
                r   .path("/api/note/**")
                    .uri("lb://service-notes")
            )

			// Configuration pour aller vers commentaire service
            .route(r ->
                r   .path("/api/password/**")
                    .uri("lb://service-gestion-password")
            )


            .route(r ->
                r   .path("/api/passwordstolen")
                    .uri("lb://service-gestion-password")
            )

            .build();
    }

        @Bean
    CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        
        // GET, POST & OPTIONS dans applyPermitDefaultValues
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.PATCH);

        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
