package com.github.matek2305.betting.match.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.match.domain.Match;
import com.github.matek2305.betting.match.domain.*;
import com.github.matek2305.betting.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.match.domain.MatchEvent.NewMatchAdded;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class InMemoryMatchRepository implements MatchRepository, FindIncomingMatch {

    private final Map<MatchId, Match> matches = new ConcurrentHashMap<>();

    private final MatchBettingPolicies bettingPolicies;
    private final EventsPublisher publisher;

    @Override
    public Option<Match> findBy(MatchId matchId) {
        return Option.of(matches.get(matchId));
    }

    @Override
    public Match publish(MatchEvent event) {
        var match = Match(event).of(
                Case($(instanceOf(NewMatchAdded.class)), this::createNewIncomingMatch),
                Case($(instanceOf(MatchFinished.class)), this::finishMatch));

        publisher.publish(event);
        return match;
    }

    @Override
    public Option<IncomingMatch> findIncomingMatchBy(MatchId matchId) {
        return Match(findBy(matchId)).of(
                Case($Some($(instanceOf(IncomingMatch.class))), Option::of),
                Case($(), Option::none));
    }

    private Match createNewIncomingMatch(NewMatchAdded newMatchAdded) {
        Match match = new IncomingMatch(
                new MatchInformation(
                        newMatchAdded.matchId(),
                        newMatchAdded.startDateTime(),
                        newMatchAdded.rivals()),
                bettingPolicies.bettingAllowedBeforeMatchStartOnly());

        matches.put(match.matchId(), match);
        return match;
    }

    private Match finishMatch(MatchFinished matchFinished) {
        return matches.computeIfPresent(matchFinished.matchId(),
                ((matchId, match) -> new FinishedMatch(match.matchInformation(), matchFinished.result())));
    }
}
