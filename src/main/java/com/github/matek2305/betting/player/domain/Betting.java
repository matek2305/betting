package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.FindIncomingMatch;
import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Betting {

    private final FindIncomingMatch findIncomingMatch;
    private final Players players;

    public void makeBet(MakeBetCommand command) {
        var match = get(command.matchId());
        var player = get(command.playerId());
        players.publish(player.placeBet(match, command.bet()));
    }

    private IncomingMatch get(MatchId matchId) {
        return findIncomingMatch.findIncomingMatchBy(matchId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find incoming match with id=" + matchId));
    }

    private Player get(PlayerId playerId) {
        return players.findBy(playerId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find player with id=" + playerId));
    }
}
