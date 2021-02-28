package com.github.matek2305.betting.core.player.web;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Set;

@Value
class PlayerResponse {
    String name;
    Set<MatchBet> activeBets;
    Set<ScoredPoints> scoredPoints;

    @Value
    static class MatchBet {
        String homeTeamName;
        String awayTeamName;
        ZonedDateTime startDateTime;
        Score bet;
    }

    @Value
    static class ScoredPoints {
        MatchBet bet;
        Score result;
        int points;

    }

    @Value
    static class Score {
        int homeTeam;
        int awayTeam;
    }
}
