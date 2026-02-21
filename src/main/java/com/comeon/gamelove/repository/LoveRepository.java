package com.comeon.gamelove.repository;

import com.comeon.gamelove.entity.Love;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoveRepository extends JpaRepository<Love, Long> {

    Optional<Love> findByPlayerIdAndGameId(Long playerId, Long gameId);

    @Query("select l.game as game, count(l) as loveCount from Love l group by l.game order by count(l) desc")
    Page<TopGameProjection> findTopLovedGames(Pageable pageable);

    List<Love> findByPlayerId(Long playerId);
}