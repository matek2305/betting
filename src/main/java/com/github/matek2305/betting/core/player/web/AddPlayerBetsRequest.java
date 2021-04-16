package com.github.matek2305.betting.core.player.web;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
class AddPlayerBetsRequest {
    Set<SingleMatchBetRequest> bets;

    @Value
    static class SingleMatchBetRequest {
        UUID matchId;
        int homeTeamScore;
        int awayTeamScore;
    }
}
