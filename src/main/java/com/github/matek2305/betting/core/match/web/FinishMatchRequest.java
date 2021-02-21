package com.github.matek2305.betting.core.match.web;

import lombok.Value;

import java.util.UUID;

@Value
class FinishMatchRequest {
    UUID matchId;
    int homeTeamScore;
    int awayTeamScore;
}
