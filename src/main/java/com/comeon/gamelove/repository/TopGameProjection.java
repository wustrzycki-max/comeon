package com.comeon.gamelove.repository;

import com.comeon.gamelove.entity.Game;

public interface TopGameProjection {
    Game getGame();
    long getLoveCount();
}