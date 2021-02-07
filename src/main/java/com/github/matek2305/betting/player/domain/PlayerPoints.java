package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@RequiredArgsConstructor
public class PlayerPoints {
    Map<MatchId, BetPoints> points;

    public PlayerPoints() {
        this(Collections.emptyMap());
    }
}
