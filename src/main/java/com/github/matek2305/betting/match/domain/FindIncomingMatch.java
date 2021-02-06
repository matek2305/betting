package com.github.matek2305.betting.match.domain;

import io.vavr.control.Option;

public interface FindIncomingMatch {

    Option<IncomingMatch> findIncomingMatchBy(MatchId matchId);
}
