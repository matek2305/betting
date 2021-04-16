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
    PlayerBet bet;

    static MatchView noBet(
            UUID matchId,
            String homeTeamName,
            String awayTeamName,
            ZonedDateTime when
    ) {
        return new MatchView(matchId, homeTeamName, awayTeamName, when, null);
    }

    @Value
    public static class PlayerBet {
        int homeTeam;
        int awayTeam;
    }
}
