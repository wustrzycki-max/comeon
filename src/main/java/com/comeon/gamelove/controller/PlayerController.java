package com.comeon.gamelove.controller;


import com.comeon.gamelove.dto.PlayerDto;
import com.comeon.gamelove.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerDto create(@RequestBody PlayerDto req) {
        return playerService.create(req);
    }

    @GetMapping
    public List<PlayerDto> getAll() {
        return playerService.getAll();
    }
}