package com.cluborg.coreplayerservice.web;

import com.cluborg.coreplayerservice.model.Player;
import com.cluborg.coreplayerservice.repository.PlayerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest
@Import({PlayerRestConfiguration.class, PlayerHandler.class})
@ActiveProfiles("test")
public class PlayerControllerTest {

    @MockBean
    private PlayerRepository playerRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldGetAllPlayers(){

        Mockito.when(playerRepository.findAll())
                .thenReturn(Flux.just(new Player(null, "Colm", "Murphy", "Email", "teamID")));

        this.webTestClient
                .get()
                .uri("/players")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody().jsonPath("@.[0].firstName").isEqualTo("Colm");
    }

    @Test
    public void shouldGetOnePlayer(){

        Mockito.when(playerRepository.findById("myID"))
                .thenReturn(Mono.just(new Player("myID", "Colm", "Murphy", "Email", "teamId")));

        this.webTestClient
                .get()
                .uri("/player/myID")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody().jsonPath("@.firstName").isEqualTo("Colm");
    }

    @Test
    public void shouldSaveOnePlayer(){
        Player playerMono = new Player(null, "James", "OConnel", "test@username.com", "teamId");

        Mockito.when(playerRepository.save(playerMono))
                .thenReturn(Mono.just(playerMono));

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void shouldUpdateOnePlayer(){
        //given
        Player savedPlayer = new Player("id", "Colm", "Murphy", "colm@Test.com", "NaomhAban");
        Player playersUpdate = new Player("id", "Colm", "Murphy", "colmMurphy@Test.com", "NaomhAban");

        //when
        Mockito.when(playerRepository.findById("id")).thenReturn(Mono.just(savedPlayer));
        Mockito.when(playerRepository.save(playersUpdate)).thenReturn(Mono.just(playersUpdate));

        //then
        webTestClient
                .put()
                .uri("/player/id")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(playersUpdate), Player.class)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody()
                .jsonPath("@.firstName")
                .isEqualTo("Colm");
    }
    @Test
    public void shouldUReturnBadRequestForUpdate(){
        //given
        Player playersUpdate = new Player("id", "", "Murphy", "colmMurphy@Test.com", "NaomhAban");

        //then
        webTestClient
                .put()
                .uri("/player/id")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(playersUpdate), Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody();
    }


    @Test
    public void shouldReturnNotFound(){
        Mockito.when(playerRepository.findById("id"))
                .thenReturn(Mono.empty());

        this.webTestClient
                .get()
                .uri("/player/id")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void shouldReturnAlreadyExists(){
        Player playerMono = new Player(null, "James", "OConnel", "test", "teamId");

        Mockito.when(playerRepository.save(playerMono))
                .thenReturn(Mono.error(new RuntimeException("")));

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void shouldReturnBadRequestForEmptyFirstName(){
        Player playerMono = new Player(null, "", "asdasd", "test", "teamID");

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestForEmptyLastNameName(){
        Player playerMono = new Player(null, "asdasd", "", "test", "teamId");

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestForEmptyEmail(){
        Player playerMono = new Player(null, "asdasd", "asdasd", "", "teamId");

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestForEmptyInvalidEmail(){
        Player playerMono = new Player(null, "asdasdasd", "asdasd", "test", "teamId");

        this.webTestClient
                .post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(playerMono), Player.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void shouldReturnAListOfPlayersByTeamId(){

        Mockito.when(playerRepository.findByTeamId("teamId"))
                .thenReturn(Flux.just(new Player("myID1", "Colm", "Murphy", "Email", "teamId"), new Player("myID2", "John", "Murphy", "Email", "teamId")));

        this.webTestClient
                .get()
                .uri("/players?teamId=teamId")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody().jsonPath("@.[0].firstName").isEqualTo("Colm");
    }

    @Test
    public void shouldDeleteAPlayer(){

        Mockito.when(playerRepository.findById("myID"))
                .thenReturn(Mono.just(new Player("myID", "Colm", "Murphy", "Email", "teamId")));

        Mockito.when(playerRepository.delete(new Player("myID", "Colm", "Murphy", "Email", "teamId")))
                .thenReturn(Mono.empty());

        this.webTestClient
                .delete()
                .uri("/player/myID")
                .exchange()
                .expectStatus()
                .isNoContent();

    }
}
