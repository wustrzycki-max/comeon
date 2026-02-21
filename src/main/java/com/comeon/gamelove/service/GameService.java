package com.comeon.gamelove.service;

import com.comeon.gamelove.dto.GameDto;
import com.comeon.gamelove.entity.Game;
import com.comeon.gamelove.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepo;

    public GameDto create(GameDto gameDto) {
        Game g = new Game();
        g.setName(gameDto.name());
        g = gameRepo.save(g);
        return new GameDto(g.getId(), g.getName());
    }

    public List<GameDto> getAll() {
        return gameRepo.findAll().stream()
                .map(g -> new GameDto(g.getId(), g.getName()))
                .toList();
    }
}
