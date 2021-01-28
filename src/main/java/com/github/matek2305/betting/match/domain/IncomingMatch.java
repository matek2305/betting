package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.player.domain.PlayerEvent.PlayerBetMade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IncomingMatch implements Match {

    private final MatchInformation matchInformation;
    private final PlayerBets bets;

    @Override
    public MatchInformation getMatchInformation() {
        return matchInformation;
    }

    public IncomingMatch handle(PlayerBetMade playerBetMade) {
        return new IncomingMatch(
                matchInformation,
                bets.add(playerBetMade.playerId(), playerBetMade.bet())
        );
    }
}
