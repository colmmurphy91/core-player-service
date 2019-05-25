package com.cluborg.coreplayerservice.integration;

import com.cluborg.coreplayerservice.CorePlayerServiceApplication;
import com.cluborg.coreplayerservice.model.Player;
import com.cluborg.coreplayerservice.repository.PlayerRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes= CorePlayerServiceApplication.class,
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PlayerIT {

    @LocalServerPort
    int port;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void test0_shouldGetAStatusIsOk(){
        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .get()
                .uri("players")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void test_1shouldSaveAPlayerAndAStatusCreated(){
        Player player = new Player(null, "Colm", "Murphy", "test@username.com", "teamId");
        Mono<Player> playerMono = Mono.just(player);
        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .post()
                .uri("player")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(playerMono, Player.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void test_2shouldThrowErrorForInvalidEmailAddress(){
        Player player = new Player(null, "Colm", "Murphy", "test", "teamId");
        Mono<Player> playerMono = Mono.just(player);
        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .post()
                .uri("player")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(playerMono, Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().jsonPath("@.message").isEqualTo("must be a well-formed email address");
    }

    @Test
    public void test_3givenINeedToUpdateEmail_WhenIUpdateEmail_thenIWillReceive202(){
        Player playerSaved = new Player(null, "Colm", "Murphy", "test@username.com", "teamId");
        Mono<Player> playerMono = playerRepository.save(playerSaved);

        String id = playerMono.block().getId();

        Player updatePlayer = new Player(null, "Colm", "Murphy", "colm@username.com", "teamId");

        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .put()
                .uri("player/" + id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(updatePlayer), Player.class)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody().jsonPath("@.email").isEqualTo("colm@username.com");
    }

    @Test
    public void test_4givenAPLayerID_WhenITryToDelete_ThenIWillReceiveANoContent(){
        Player playerSaved = new Player(null, "Colm", "Murphy", "test@username.com", "teamId");
        Mono<Player> playerMono = playerRepository.save(playerSaved);

        String id = playerMono.block().getId();

        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .delete()
                .uri("player/" + id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    public void test_5givenAPLayerIDThatDoesNotExist_WhenITryToDelete_ThenIWillReceiveABadRequest(){
        String id = "nonId";

        WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build()
                .delete()
                .uri("player/" + id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }



}

