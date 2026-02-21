package com.comeon.gamelove.repository;

import com.comeon.gamelove.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {}