package com.github.matek2305.betting.match.domain;

import io.vavr.control.Option;

public interface MatchRepository {

    Option<Match> findBy(MatchId matchId);

    void save(Match match);
}
