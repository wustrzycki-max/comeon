package com.comeon.gamelove.controller;

import com.comeon.gamelove.dto.GameDto;
import com.comeon.gamelove.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto create(@RequestBody GameDto req) {
        return gameService.create(req);
    }

    @GetMapping
    public List<GameDto> getAll() {
        return gameService.getAll();
    }
}