package com.github.matek2305.betting.core.match.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FinishMatch {

    private final IncomingMatches incomingMatches;
    private final MatchRepository matchRepository;

    void finishMatch(FinishMatchCommand command) {
        var match = get(command.matchId());
        matchRepository.publish(match.finish(command));
    }

    private IncomingMatch get(MatchId matchId) {
        return incomingMatches.findIncomingMatchBy(matchId)
                .getOrElseThrow(() -> new IllegalArgumentException("Incoming match with id=" + matchId + " not found."));
    }
}
