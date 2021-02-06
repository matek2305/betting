package com.github.matek2305.betting.match.domain;

import lombok.Value;

@Value
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
