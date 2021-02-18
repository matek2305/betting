package com.github.matek2305.betting.core.player.web;

import lombok.Value;

import java.util.UUID;

@Value
class AddPlayerBetRequest {
    UUID matchId;
    int homeTeamScore;
    int awayTeamScore;
}
