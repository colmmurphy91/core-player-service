package com.cluborg.coreplayerservice.repository;

import com.cluborg.coreplayerservice.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void shouldSaveAndReturnByEmail(){
        Player player = new Player(null, "Colm", "Murphy", "colm@test.com", "Naomh Aban");

        Mono<Player> playerMono = playerRepository.save(player);

        Publisher<Player> playerPublisher = playerMono.then(playerRepository.findByEmail(player.getEmail()));

        StepVerifier
                .create(playerPublisher)
                .expectNextMatches(player1 -> player1.getFirstName().equals(player.getFirstName()))
                .verifyComplete();
    }

    @Test
    public void shouldSaveAndReturnByTeamId(){
        Player player = new Player(null, "Colm", "Murphy", "colm@test.com", "Naomh Aban");

        Flux<Player> playerFlux = Flux.just(player);

        Flux<Player> savedFlux = playerRepository.saveAll(playerFlux);

        Publisher<Player> playerPublisher = savedFlux.thenMany(playerRepository.findByTeamId("Naomh Aban"));

        StepVerifier
                .create(playerPublisher)
                .expectNextMatches(player1 -> player1.getFirstName().equals(player.getFirstName()))
                .expectComplete();
    }
}
