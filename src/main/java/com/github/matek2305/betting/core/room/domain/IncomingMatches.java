package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.external.ExternalMatch;

import java.util.List;

public interface IncomingMatches {

    void publish(AddIncomingMatchEvent event);

    IncomingMatch getIncomingMatchBy(MatchId matchId);

    List<Match> findNext(int howMany);

    List<Match> findStarted(int howMany);
}
