package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.match.domain.MatchEvent.MatchFinished;
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

    public MatchFinished finish(FinishMatchCommand command) {
        return new MatchFinished(command.matchId(), command.result());
    }
}
