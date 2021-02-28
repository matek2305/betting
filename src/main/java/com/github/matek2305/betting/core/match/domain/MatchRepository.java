package com.github.matek2305.betting.core.match.domain;

import io.vavr.API;
import io.vavr.control.Option;

import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static java.util.function.Function.identity;

public interface MatchRepository {

    default <T extends Match> T getBy(MatchId matchId, Class<T> type) {
        return API.Match(findBy(matchId)).of(
                Case($Some($(instanceOf(type))), identity()),
                Case($(), () -> {
                    throw new MatchNotFoundException(matchId);
                }));
    }

    Option<Match> findBy(MatchId matchId);

    Set<Match> findBy(Set<MatchId> matchIds);

    void publish(MatchEvent event);
}
