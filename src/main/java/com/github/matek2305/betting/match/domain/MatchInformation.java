package com.github.matek2305.betting.match.domain;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class MatchInformation {
    MatchId matchId;
    ZonedDateTime startDateTime;
}
