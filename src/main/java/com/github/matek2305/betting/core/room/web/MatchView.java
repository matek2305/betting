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
}
