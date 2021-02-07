package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Value;

@Value
public class BetPoints {
    MatchScore bet;
    int points;
}
