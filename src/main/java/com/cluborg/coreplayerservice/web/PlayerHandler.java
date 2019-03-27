package com.cluborg.coreplayerservice.web;

import com.cluborg.coreplayerservice.exceptions.PlayerAlreadyExistsException;
import com.cluborg.coreplayerservice.model.Player;
import com.cluborg.coreplayerservice.repository.PlayerRepository;
import com.cluborg.coreplayerservice.validator.AbstractValidationHandler;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@Component
@Slf4j
public class PlayerHandler extends AbstractValidationHandler<Player, Validator>{

    private final PlayerRepository playerRepository;

    public PlayerHandler(PlayerRepository playerRepository, @Qualifier("defaultValidator") @Autowired Validator validator) {
        super(Player.class, validator);
        this.playerRepository = playerRepository;
    }

    public Mono<ServerResponse> getPlayers(ServerRequest serverRequest) {
        Flux<Player> playerFlux = serverRequest.queryParam("teamId")
                .map(playerRepository::findByTeamId)
                .orElseGet(playerRepository::findAll);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(playerFlux, Player.class);
    }


    public Mono<ServerResponse> getPlayerByID(ServerRequest serverRequest) {
        Mono<Player> playerMono = playerRepository.findById(serverRequest.pathVariable("id"));
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return playerMono
                .flatMap(player -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(playerMono, Player.class))
                .switchIfEmpty(notFound);
    }

    @Override
    public Mono<ServerResponse> updatePlayer(Player player, ServerRequest serverRequest) {
        return
                Mono.zip(
                        (data) -> {
                            Player old = (Player) data[0];
                            Player update = (Player) data[1];
                            update.setId(old.getId());
                            return update;
                        },
                        playerRepository.findById(serverRequest.pathVariable("id")),
                        Mono.just(player)
                )
                .cast(Player.class)
                .flatMap(playerRepository::save)
                .flatMap(pl -> ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON_UTF8).syncBody(pl));
    }

    @Override
    public Mono<ServerResponse> processPostBody(Player validBody, ServerRequest originalRequest) {
        return ServerResponse
                .created(URI.create(""))
                .body(savePlayer(validBody)
                        .onErrorResume(e ->
                                Mono.error(new PlayerAlreadyExistsException(
                                        HttpStatus.BAD_REQUEST, "Player already exists", e))), Player.class);
    }

    private Mono<Player> savePlayer(Player player) {
        try{
            return playerRepository.save(player);
        } catch (MongoWriteException e) {
            return Mono.error(e);
        }
    }


    public Mono<ServerResponse> deletePlayer(ServerRequest serverRequest) {
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return playerRepository
                .findById(serverRequest.pathVariable("id"))
                .flatMap(p -> noContent().build(playerRepository.delete(p)))
                .switchIfEmpty(notFound);
    }
}
