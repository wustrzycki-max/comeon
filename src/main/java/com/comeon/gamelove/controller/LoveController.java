package com.comeon.gamelove.controller;

import com.comeon.gamelove.dto.GameDto;
import com.comeon.gamelove.dto.LoveRequest;
import com.comeon.gamelove.dto.TopGameDto;
import com.comeon.gamelove.service.LoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoveController {

    private final LoveService loveService;

    @PostMapping("/loves")
    @ResponseStatus(HttpStatus.CREATED)
    public void loveGame(@RequestBody LoveRequest request) {
        loveService.loveGame(request.playerId(), request.gameId());
    }

    @DeleteMapping("/loves")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unloveGame(@RequestParam Long playerId,
                           @RequestParam Long gameId) {
        loveService.unloveGame(playerId, gameId);
    }

    @GetMapping("/players/{playerId}/games")
    public List<GameDto> getLovedGames(@PathVariable Long playerId) {
        return loveService.getLovedGamesForPlayer(playerId).stream()
                .map(g -> new GameDto(g.getId(), g.getName()))
                .toList();
    }

    @GetMapping("/games/top")
    public List<TopGameDto> getTopGames(@RequestParam(defaultValue = "10") int limit) {
        return loveService.getTopLovedGames(limit);
    }
}