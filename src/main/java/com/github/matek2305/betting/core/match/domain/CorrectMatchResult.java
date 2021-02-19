package com.github.matek2305.betting.core.match.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CorrectMatchResult {

    private final FinishedMatches finishedMatches;
    private final MatchRepository matchRepository;

    void correctResult(CorrectMatchResultCommand command) {
        var match = get(command.matchId());
        matchRepository.publish(match.correctResult(command));
    }

    private FinishedMatch get(MatchId matchId) {
        return finishedMatches.getFinishedMatchBy(matchId);
    }
}
