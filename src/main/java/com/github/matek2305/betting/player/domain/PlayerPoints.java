package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@Getter(AccessLevel.NONE)
@RequiredArgsConstructor
public class PlayerPoints {
    Map<MatchId, BetPoints> points;

    public PlayerPoints() {
        this(Collections.emptyMap());
    }

    PlayerPoints with(MatchId matchId, BetPoints points) {
        return new PlayerPoints(
                new ImmutableMap.Builder<MatchId, BetPoints>()
                        .putAll(this.points)
                        .put(matchId, points)
                        .build());
    }
}
