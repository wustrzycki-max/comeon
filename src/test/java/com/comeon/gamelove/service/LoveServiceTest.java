package com.comeon.gamelove.service;

import com.comeon.gamelove.dto.TopGameDto;
import com.comeon.gamelove.entity.Game;
import com.comeon.gamelove.entity.Love;
import com.comeon.gamelove.entity.Player;
import com.comeon.gamelove.exception.BusinessException;
import com.comeon.gamelove.exception.NotFoundException;
import com.comeon.gamelove.repository.GameRepository;
import com.comeon.gamelove.repository.LoveRepository;
import com.comeon.gamelove.repository.PlayerRepository;
import com.comeon.gamelove.repository.TopGameProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoveServiceTest {

    private PlayerRepository playerRepo;
    private GameRepository gameRepo;
    private LoveRepository loveRepo;
    private LoveService loveService;

    @BeforeEach
    void setUp() {
        playerRepo = mock(PlayerRepository.class);
        gameRepo = mock(GameRepository.class);
        loveRepo = mock(LoveRepository.class);
        loveService = new LoveService(playerRepo, gameRepo, loveRepo);
    }

    @Test
    void shouldSaveLoveWhenPlayerAndGameExistAndNotAlreadyLoved() {
        Long playerId = 1L;
        Long gameId = 2L;

        Player player = new Player();
        player.setId(playerId);

        Game game = new Game();
        game.setId(gameId);

        when(playerRepo.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(loveRepo.findByPlayerIdAndGameId(playerId, gameId)).thenReturn(Optional.empty());

        loveService.loveGame(playerId, gameId);

        ArgumentCaptor<Love> captor = ArgumentCaptor.forClass(Love.class);
        verify(loveRepo).save(captor.capture());

        Love saved = captor.getValue();
        assertThat(saved.getPlayer()).isEqualTo(player);
        assertThat(saved.getGame()).isEqualTo(game);
    }

    @Test
    void shouldThrowNotFoundWhenPlayerNotFound() {
        Long playerId = 1L;
        Long gameId = 2L;

        when(playerRepo.findById(playerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loveService.loveGame(playerId, gameId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Player not found");

        verifyNoInteractions(gameRepo);
        verifyNoInteractions(loveRepo);
    }

    @Test
    void shouldThrowNotFoundWhenGameNotFound() {
        Long playerId = 1L;
        Long gameId = 2L;

        Player player = new Player();
        player.setId(playerId);

        when(playerRepo.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepo.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loveService.loveGame(playerId, gameId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Game not found");

        verify(loveRepo, never()).findByPlayerIdAndGameId(anyLong(), anyLong());
        verify(loveRepo, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAlreadyLoved() {
        Long playerId = 1L;
        Long gameId = 2L;

        Player player = new Player();
        player.setId(playerId);

        Game game = new Game();
        game.setId(gameId);

        Love existingLove = new Love();
        existingLove.setPlayer(player);
        existingLove.setGame(game);

        when(playerRepo.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(loveRepo.findByPlayerIdAndGameId(playerId, gameId))
                .thenReturn(Optional.of(existingLove));

        assertThatThrownBy(() -> loveService.loveGame(playerId, gameId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already loved");

        verify(loveRepo, never()).save(any());
    }

    @Test
    void shouldDeleteLoveWhenRelationExists() {
        Long playerId = 1L;
        Long gameId = 2L;

        Love love = new Love();
        when(loveRepo.findByPlayerIdAndGameId(playerId, gameId))
                .thenReturn(Optional.of(love));

        loveService.unloveGame(playerId, gameId);

        verify(loveRepo).delete(love);
    }

    @Test
    void shouldThrowNotFoundWhenRelationDoesNotExist() {
        Long playerId = 1L;
        Long gameId = 2L;

        when(loveRepo.findByPlayerIdAndGameId(playerId, gameId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loveService.unloveGame(playerId, gameId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Love relation not found");

        verify(loveRepo, never()).delete(any());
    }

    @Test
    void shouldReturnGamesFromLoveRelations() {
        Long playerId = 1L;

        Game game1 = new Game();
        game1.setId(10L);
        Game game2 = new Game();
        game2.setId(20L);

        Love love1 = new Love();
        love1.setGame(game1);
        Love love2 = new Love();
        love2.setGame(game2);

        when(loveRepo.findByPlayerId(playerId)).thenReturn(List.of(love1, love2));

        List<Game> result = loveService.getLovedGamesForPlayer(playerId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Game::getId)
                .containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void shouldReturnEmptyListWhenNoLoves() {
        Long playerId = 1L;

        when(loveRepo.findByPlayerId(playerId)).thenReturn(List.of());

        List<Game> result = loveService.getLovedGamesForPlayer(playerId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapProjectionToDto() {
        int limit = 5;

        Game game1 = new Game();
        game1.setId(10L);
        Game game2 = new Game();
        game2.setId(20L);

        TopGameProjection proj1 = mock(TopGameProjection.class);
        TopGameProjection proj2 = mock(TopGameProjection.class);
        when(proj1.getGame()).thenReturn(game1);
        when(proj1.getLoveCount()).thenReturn(3L);
        when(proj2.getGame()).thenReturn(game2);
        when(proj2.getLoveCount()).thenReturn(7L);

        Page<TopGameProjection> page =
                new PageImpl<>(List.of(proj1, proj2), PageRequest.of(0, limit), 2);

        when(loveRepo.findTopLovedGames(PageRequest.of(0, limit))).thenReturn(page);

        List<TopGameDto> result = loveService.getTopLovedGames(limit);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(TopGameDto::gameId, TopGameDto::loveCount)
                .containsExactlyInAnyOrder(
                        tuple(10L, 3L),
                        tuple(20L, 7L)
                );
    }

    @Test
    void shouldReturnEmptyListWhenNoResults() {
        int limit = 3;

        Page<TopGameProjection> emptyPage =
                new PageImpl<>(List.of(), PageRequest.of(0, limit), 0);

        when(loveRepo.findTopLovedGames(PageRequest.of(0, limit))).thenReturn(emptyPage);

        List<TopGameDto> result = loveService.getTopLovedGames(limit);

        assertThat(result).isEmpty();
    }
}
