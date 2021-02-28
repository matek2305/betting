package com.github.matek2305.betting.core.match.domain;

import java.util.Set;

public interface FinishedMatches {

    FinishedMatch getFinishedMatchBy(MatchId matchId);

    Set<FinishedMatch> getFinishedMatchesBy(Set<MatchId> matchIds);
}
