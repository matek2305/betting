package com.github.matek2305.betting.core.match.domain;

import io.vavr.control.Option;

import java.util.List;
import java.util.Set;

public interface MatchRepository {

    Option<Match> findBy(MatchId matchId);

    List<Match> findAll();

    Set<Match> findBy(Set<MatchId> matchIds);

    void publish(MatchEvent event);
}
