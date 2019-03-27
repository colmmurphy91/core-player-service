package com.cluborg.coreplayerservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PlayerRestConfiguration {

    @Bean
    RouterFunction<?> route(PlayerHandler playerHandler) {
        return RouterFunctions
                .route(GET("/players"), playerHandler::getPlayers)
                .andRoute(GET("/player/{id}"), playerHandler::getPlayerByID)
                .andRoute(POST("/player"), playerHandler::handleRequest)
                .andRoute(PUT("/player/{id}"), playerHandler::handleRequest)
                .andRoute(DELETE("/player/{id}"), playerHandler::deletePlayer);
    }
}
