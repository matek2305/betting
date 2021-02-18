package com.github.matek2305.betting.core.player.web;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Set;

@Value
class PlayerResponse {
    String name;
    Set<ActiveBet> activeBets;

    @Value
    static class ActiveBet {
        String homeTeamName;
        String awayTeamName;
        ZonedDateTime startDateTime;
        int homeTeamScoreBet;
        int awayTeamScoreBet;
    }
}
