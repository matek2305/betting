package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchScore;
import lombok.Value;

@Value
public class BetPoints {
    MatchScore bet;
    Points points;
}
