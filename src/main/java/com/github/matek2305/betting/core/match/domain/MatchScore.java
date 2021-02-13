package com.github.matek2305.betting.core.match.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class MatchScore {
    int homeTeamScore;
    int awayTeamScore;

    public boolean homeTeamWon() {
        return homeTeamScore > awayTeamScore;
    }

    public boolean awayTeamWon() {
        return awayTeamScore > homeTeamScore;
    }

    public boolean draw() {
        return homeTeamScore == awayTeamScore;
    }
}
