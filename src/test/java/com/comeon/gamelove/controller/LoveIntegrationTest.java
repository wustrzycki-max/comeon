package com.comeon.gamelove.controller;

import com.comeon.gamelove.GameloveApplication;
import com.comeon.gamelove.dto.GameDto;
import com.comeon.gamelove.dto.LoveRequest;
import com.comeon.gamelove.dto.PlayerDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GameloveApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoveIntegrationTest {

    protected MockMvc mockMvc;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    private List<PlayerDto> players;
    private List<GameDto> games;

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    void shouldCreateGames() throws Exception {
        createGame("Charades");
        createGame("Memory");
        createGame("Silent Phone");
    }

    @Test
    @Order(2)
    void shouldCreatePlayers() throws Exception {
        createPlayer("Alice");
        createPlayer("Bruce");
        createPlayer("John");
    }

    @Test
    @Order(3)
    void shouldFetchAllPlayers() throws Exception {
        String playersResStr = mockMvc.perform(get("/api/players"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        players = objectMapper.readValue(playersResStr, new TypeReference<>() {
        });
    }

    @Test
    @Order(4)
    void shouldFetchAllGames() throws Exception {
        String gamesResStr = mockMvc.perform(get("/api/games"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        games = objectMapper.readValue(gamesResStr, new TypeReference<>() {
        });
    }

    @Test
    @Order(5)
    void shouldFallInLoveWithGames() throws Exception {
        loveGame(getPlayerId("Alice"), getGameId("Charades"));
        loveGame(getPlayerId("Bruce"), getGameId("Charades"));
        loveGame(getPlayerId("John"), getGameId("Charades"));

        loveGame(getPlayerId("Alice"), getGameId("Memory"));
        loveGame(getPlayerId("Bruce"), getGameId("Memory"));

        loveGame(getPlayerId("Alice"), getGameId("Silent Phone"));
    }

    @Test
    @Order(6)
    void shouldAliceLove3Games() throws Exception {
        mockMvc.perform(get(String.format("/api/players/%s/games", getPlayerId("Alice")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3))); //loves 3 games
    }

    @Test
    @Order(7)
    void shouldAliceUnloveGames() throws Exception {
        mockMvc.perform(delete("/api/loves")
                        .param("playerId", String.valueOf(getPlayerId("Alice")))
                        .param("gameId", String.valueOf(getGameId("Memory")))
                )
                .andExpect(status().isNoContent());
        mockMvc.perform(get(String.format("/api/players/%s/games", getPlayerId("Alice")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2))); //now loves only 2
    }

    @Test
    @Order(8)
    void shouldReturnMostLovedGames() throws Exception {
        mockMvc.perform(get("/api/games/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].gameId").value(getGameId("Charades"))) // most loved game on first position
                .andExpect(jsonPath("$[0].loveCount").value(3)); // all 3 players love Charades
    }


    private Long getPlayerId(String playerName) {
        return players.stream().filter(p -> p.name().equals(playerName))
                .map(PlayerDto::playerId).findAny().orElse(null);
    }

    private Long getGameId(String gameName) {
        return games.stream().filter(p -> p.name().equals(gameName))
                .map(GameDto::gameId).findAny().orElse(null);
    }

    private void loveGame(Long playerId, Long gameId) throws Exception {
        String req = objectMapper.writeValueAsString(new LoveRequest(playerId, gameId));
        mockMvc.perform(post("/api/loves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isCreated());
    }


    private void createPlayer(String playerName) throws Exception {
        String req = objectMapper.writeValueAsString(new PlayerDto(null, playerName));
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerId").exists())
                .andExpect(jsonPath("$.name").value(playerName));
    }

    private void createGame(String gameName) throws Exception {
        String req = objectMapper.writeValueAsString(new GameDto(null, gameName));
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.name").value(gameName));
    }
}
