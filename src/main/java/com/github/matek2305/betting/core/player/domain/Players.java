package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchId;
import io.vavr.control.Option;

import java.util.Set;

public interface Players {

    Player createWithId(PlayerId playerId);

    default Player getBy(PlayerId playerId) {
        return findBy(playerId)
                .getOrElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    Option<Player> findBy(PlayerId playerId);

    Set<Player> findByBetMatchId(MatchId matchId);

    void publish(PlayerEvent result);
}
