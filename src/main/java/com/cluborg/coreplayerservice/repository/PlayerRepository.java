package com.cluborg.coreplayerservice.repository;

import com.cluborg.coreplayerservice.model.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveMongoRepository <Player, String>{

    Mono<Player> findByEmail(String email);

    Flux<Player> findByTeamId(String teamId);
}
