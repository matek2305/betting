package com.github.matek2305.betting.core.match.domain;

import io.vavr.control.Option;

import java.util.List;

public interface MatchRepository {

    Option<Match> findBy(MatchId matchId);

    List<Match> findAll();

    void publish(MatchEvent event);
}
