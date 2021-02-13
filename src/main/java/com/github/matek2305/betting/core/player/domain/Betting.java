package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatches;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Betting {

    private final IncomingMatches incomingMatches;
    private final Players players;

    public void makeBet(MakeBetCommand command) {
        var match = get(command.matchId());
        var player = get(command.playerId());
        players.publish(player.placeBet(match, command.bet()));
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.findIncomingMatchBy(matchId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find incoming match with id=" + matchId));
    }

    private Player get(PlayerId playerId) {
        return players.findBy(playerId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find player with id=" + playerId));
    }
}