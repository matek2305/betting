package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.match.domain.FindIncomingMatch;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchBettingPolicies;
import com.github.matek2305.betting.core.match.domain.MatchEvent;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.match.domain.MatchEvent.NewMatchAdded;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
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
    public void publish(MatchEvent event) {
        var match = API.Match(event).of(
                Case($(instanceOf(NewMatchAdded.class)), this::createNewIncomingMatch),
                Case($(instanceOf(MatchFinished.class)), this::finishMatch));

        matches.put(match.matchId(), match);
        publisher.publish(event);
    }

    @Override
    public Option<IncomingMatch> findIncomingMatchBy(MatchId matchId) {
        return API.Match(findBy(matchId)).of(
                Case($Some($(instanceOf(IncomingMatch.class))), Option::of),
                Case($(), Option::none));
    }

    private Match createNewIncomingMatch(NewMatchAdded newMatchAdded) {
        return new IncomingMatch(
                new MatchInformation(
                        newMatchAdded.matchId(),
                        newMatchAdded.startDateTime()),
                bettingPolicies.bettingAllowedBeforeMatchStartOnly(),
                MatchRewardingPolicy.defaultRewards());
    }

    private Match finishMatch(MatchFinished matchFinished) {
        return findIncomingMatchBy(matchFinished.matchId())
                .map(match -> match.handle(matchFinished))
                .getOrElseThrow(() -> new IllegalArgumentException("Match with id=" + matchFinished.matchId() + " not found"));
    }
}
