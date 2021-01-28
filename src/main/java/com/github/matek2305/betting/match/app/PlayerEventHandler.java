package com.github.matek2305.betting.match.app;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.Match;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchRepository;
import com.github.matek2305.betting.player.domain.PlayerEvent.PlayerBetMade;
import lombok.RequiredArgsConstructor;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
class PlayerEventHandler {

    private final MatchRepository matchRepository;

    void handle(PlayerBetMade playerBetMade) {
        Match match = Match(find(playerBetMade.matchId())).of(
                Case($(instanceOf(IncomingMatch.class)), incomingMatch -> incomingMatch.handle(playerBetMade)),
                Case($(), m -> { throw new IllegalArgumentException("Players can bet only on incoming matches"); })
        );
        matchRepository.save(match);
    }

    private Match find(MatchId matchId) {
        return matchRepository.findBy(matchId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find match with id=" + matchId));
    }
}
