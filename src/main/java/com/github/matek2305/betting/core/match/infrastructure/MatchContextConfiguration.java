package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.core.match.domain.CorrectMatchResult;
import com.github.matek2305.betting.core.match.domain.FinishMatch;
import com.github.matek2305.betting.core.match.domain.FinishedMatches;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
class MatchContextConfiguration {

    @Produces
    public FinishMatch finishMatch(
            IncomingMatches incomingMatches, MatchRepository matchRepository) {
        return new FinishMatch(incomingMatches, matchRepository);
    }

    @Produces
    public CorrectMatchResult correctMatchResult(
            FinishedMatches finishedMatches, MatchRepository matchRepository) {
        return new CorrectMatchResult(finishedMatches, matchRepository);
    }
}
