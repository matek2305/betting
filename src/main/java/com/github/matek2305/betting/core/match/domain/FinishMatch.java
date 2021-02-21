package com.github.matek2305.betting.core.match.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinishMatch {

    private final IncomingMatches incomingMatches;
    private final MatchRepository matchRepository;

    public void finishMatch(FinishMatchCommand command) {
        var match = get(command.matchId());
        matchRepository.publish(match.finish(command));
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.getIncomingMatchBy(matchId);
    }
}
