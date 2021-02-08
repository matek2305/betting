package com.github.matek2305.betting.core.match.domain;

import io.vavr.control.Option;

public interface MatchRepository {

    Option<Match> findBy(MatchId matchId);

    void publish(MatchEvent event);
}
