package com.github.matek2305.betting.core.match.web;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class MatchResponse {
    UUID matchId;
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime startDateTime;
}
