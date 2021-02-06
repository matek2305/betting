package com.github.matek2305.betting.match.domain;

import lombok.Value;

@Value
public class MatchRewards {
    int pointsForExactResultHit;
    int pointsForWinningTeamHit;
    int pointsForDrawHit;
    int pointsForMissingBet;
}
