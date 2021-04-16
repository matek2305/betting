package com.github.matek2305.betting.core.match.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class MatchScore {
    int homeTeam;
    int awayTeam;

    public boolean homeTeamWon() {
        return homeTeam > awayTeam;
    }

    public boolean awayTeamWon() {
        return awayTeam > homeTeam;
    }

    public boolean draw() {
        return homeTeam == awayTeam;
    }
}
