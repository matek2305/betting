package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.player.domain.PlayerId;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@RequiredArgsConstructor
public class PlayerBets {

    Map<PlayerId, MatchScore> bets;

    public PlayerBets() {
        this(Collections.emptyMap());
    }

    PlayerBets add(PlayerId playerId, MatchScore bet) {
        return new PlayerBets(
                new ImmutableMap.Builder<PlayerId, MatchScore>()
                        .putAll(bets)
                        .put(playerId, bet)
                        .build()
        );
    }
}
