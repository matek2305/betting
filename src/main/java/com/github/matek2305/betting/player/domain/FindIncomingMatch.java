package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import io.vavr.control.Option;

public interface FindIncomingMatch {

    Option<IncomingMatch> findIncomingMatchBy(MatchId matchId);
}
