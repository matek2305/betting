package com.github.matek2305.betting.core.match.domain;

import java.util.List;

public interface IncomingMatches {

    IncomingMatch getIncomingMatchBy(MatchId matchId);

    List<IncomingMatch> findNext(int howMany);

    List<IncomingMatch> findStarted(int howMany);
}
