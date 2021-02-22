package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Option;
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

    public Option<BetPoints> get(MatchId matchId) {
        return Option.of(points.get(matchId));
    }

    PlayerPoints with(MatchId matchId, BetPoints points) {
        return new PlayerPoints(
                new ImmutableMap.Builder<MatchId, BetPoints>()
                        .putAll(this.points)
                        .put(matchId, points)
                        .build());
    }
}
