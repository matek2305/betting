package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import io.vavr.control.Option;

import java.util.Set;

public interface Players {

    Option<Player> findBy(PlayerId playerId);

    Set<Player> findByBetMatchId(MatchId matchId);

    Player publish(PlayerEvent result);
}
