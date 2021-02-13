package com.github.matek2305.betting.core.match.domain;

import io.vavr.control.Option;

import java.util.List;

public interface IncomingMatches {

    Option<IncomingMatch> findIncomingMatchBy(MatchId matchId);

    List<IncomingMatch> findNext(int howMany);
}
