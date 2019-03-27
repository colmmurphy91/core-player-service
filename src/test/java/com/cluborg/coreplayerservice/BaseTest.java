package com.cluborg.coreplayerservice;


import com.cluborg.coreplayerservice.model.Player;
import com.cluborg.coreplayerservice.repository.PlayerRepository;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = "server.port= 0")
@RunWith(SpringRunner.class)
@Import(CorePlayerServiceApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public abstract class BaseTest {

    static {
        System.setProperty("eureka.client.enabled", "false");
        System.setProperty("spring.cloud.config.failFast", "false");
    }


    @LocalServerPort
    private int port;

    @MockBean
    private PlayerRepository playerRepository;

    @Before
    public void before(){

        RestAssured.baseURI = "http://localhost:" + this.port;

        Mockito.when(playerRepository.findByTeamId("id"))
                .thenReturn(Flux.just(new Player("myID1", "Colm", "Murphy", "colm.j.murphy91@gmail.com", "teamId")));

        Mockito.when(playerRepository.findByTeamId("teamWithNoPlayers"))
                .thenReturn(Flux.empty());

        Mockito.when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(new Player("myID1", "Michelle", "Murphy", "michelle.murphy@gmail.com", "teamId")));

        Mockito.when(playerRepository.delete(any(Player.class)))
                .thenReturn(Mono.empty());

        Mockito.when(playerRepository.findById("myID1"))
                .thenReturn(Mono.just(new Player("myID1", "Michelle", "Murphy", "michelle.murphy@gmail.com", "teamId")));

    }
}
