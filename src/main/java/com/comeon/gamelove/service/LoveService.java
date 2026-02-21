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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoveService {

    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;
    private final LoveRepository loveRepo;

    public void loveGame(Long playerId, Long gameId) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        loveRepo.findByPlayerIdAndGameId(playerId, gameId).ifPresent(l -> {
            throw new BusinessException("Already loved");
        });
        Love love = new Love();
        love.setPlayer(player);
        love.setGame(game);
        loveRepo.save(love);
    }

    public void unloveGame(Long playerId, Long gameId) {
        Love love = loveRepo.findByPlayerIdAndGameId(playerId, gameId)
                .orElseThrow(() -> new NotFoundException("Love relation not found"));
        loveRepo.delete(love);
    }

    public List<Game> getLovedGamesForPlayer(Long playerId) {
        return loveRepo.findByPlayerId(playerId).stream()
                .map(Love::getGame)
                .toList();
    }

    public List<TopGameDto> getTopLovedGames(int limit) {
        Page<TopGameProjection> page = loveRepo.findTopLovedGames(PageRequest.of(0, limit));
        return page.stream()
                .map(p -> new TopGameDto(p.getGame().getId(), p.getLoveCount()))
                .toList();
    }
}
