package com.github.matek2305.betting.core.match.domain;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(MatchId matchId) {
        super("Match not found by " + matchId);
    }
}
