package com.comeon.gamelove.service;

import com.comeon.gamelove.dto.PlayerDto;
import com.comeon.gamelove.entity.Player;
import com.comeon.gamelove.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerDto create(PlayerDto playerDto) {
        Player p = new Player();
        p.setName(playerDto.name());
        p = playerRepository.save(p);
        return new PlayerDto(p.getId(), p.getName());
    }

    public List<PlayerDto> getAll() {
        return playerRepository.findAll().stream()
                .map(p -> new PlayerDto(p.getId(), p.getName()))
                .toList();
    }
}
