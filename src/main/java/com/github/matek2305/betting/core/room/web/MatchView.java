package com.github.matek2305.betting.core.room.web;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class MatchView {
    UUID matchId;
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime when;
    Bet bet;

    static MatchView noBet(
            UUID matchId,
            String homeTeamName,
            String awayTeamName,
            ZonedDateTime when
    ) {
        return new MatchView(matchId, homeTeamName, awayTeamName, when, null);
    }

    @Value
    public static class Bet {
        int homeTeam;
        int awayTeam;
    }
}
