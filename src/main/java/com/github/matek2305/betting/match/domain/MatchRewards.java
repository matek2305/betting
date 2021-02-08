package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.player.domain.Points;
import lombok.Value;

@Value
public class MatchRewards {
    Points pointsForExactResultHit;
    Points pointsForWinningTeamHit;
    Points pointsForDrawHit;
    Points pointsForMissingBet;
}
