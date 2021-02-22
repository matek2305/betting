package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;

import java.util.List;

public interface IncomingMatches {

    void save(IncomingMatch match);

    IncomingMatch getIncomingMatchBy(MatchId matchId);

    List<IncomingMatch> findNext(int howMany);

    List<IncomingMatch> findStarted(int howMany);
}
