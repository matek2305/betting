package com.github.matek2305.betting.livescore;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class IncomingFixtureLoaded {
    int id;
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime startDateTime;
}
