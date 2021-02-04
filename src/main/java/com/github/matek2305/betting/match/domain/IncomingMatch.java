package com.github.matek2305.betting.match.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IncomingMatch implements Match {

    @Getter
    private final MatchInformation matchInformation;
    private final MatchBettingPolicy bettingPolicy;

    public boolean isBettingAllowed() {
        return bettingPolicy.playerCanBet(this);
    }
}
