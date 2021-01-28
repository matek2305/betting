package com.github.matek2305.betting.match.infrastructure;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.Match;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchRepository;
import com.github.matek2305.betting.player.domain.FindIncomingMatch;
import io.vavr.control.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;

public class InMemoryMatchRepository implements MatchRepository, FindIncomingMatch {

    private final Map<MatchId, Match> matches = new ConcurrentHashMap<>();

    @Override
    public Option<Match> findBy(MatchId matchId) {
        return Option.of(matches.get(matchId));
    }

    @Override
    public void save(Match match) {
        matches.put(match.matchId(), match);
    }

    @Override
    public Option<IncomingMatch> findIncomingMatchBy(MatchId matchId) {
        return Match(findBy(matchId)).of(
                Case($Some($(instanceOf(IncomingMatch.class))), Option::of),
                Case($(), Option::none)
        );
    }
}
