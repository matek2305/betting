package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@RequiredArgsConstructor
public class PlayerBets {
    Map<MatchId, MatchScore> bets;

    public PlayerBets() {
        this(Collections.emptyMap());
    }
}
