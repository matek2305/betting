package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.IncomingMatches;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchEvent;
import com.github.matek2305.betting.core.match.domain.MatchEvent.IncomingMatchCreated;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchInformation;
import com.github.matek2305.betting.core.match.domain.MatchNotFoundException;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.date.DateProvider;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.github.matek2305.betting.core.match.domain.MatchBettingPolicy.bettingAllowedBeforeMatchStartOnly;
import static com.github.matek2305.betting.core.match.domain.MatchRewardingPolicy.defaultRewards;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static java.util.function.Function.identity;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class InMemoryMatchRepository implements MatchRepository, IncomingMatches {

    private final Map<MatchId, Match> matches = new ConcurrentHashMap<>();

    private final EventsPublisher publisher;
    private final DateProvider dateProvider;

    @Override
    public Option<Match> findBy(MatchId matchId) {
        return Option.of(matches.get(matchId));
    }

    @Override
    public List<Match> findAll() {
        return List.copyOf(matches.values());
    }

    @Override
    public void publish(MatchEvent event) {
        var match = API.Match(event).of(
                Case($(instanceOf(IncomingMatchCreated.class)), this::createNewIncomingMatch),
                Case($(instanceOf(MatchFinished.class)), this::finishMatch));

        matches.put(match.matchId(), match);
        publisher.publish("matches", event);
    }

    @Override
    public IncomingMatch getIncomingMatchBy(MatchId matchId) {
        return API.Match(findBy(matchId)).of(
                Case($Some($(instanceOf(IncomingMatch.class))), identity()),
                Case($(), () -> { throw new MatchNotFoundException(matchId); }));
    }

    @Override
    public List<IncomingMatch> findNext(int howMany) {
        return matches.values()
                .stream()
                .filter(match -> match.startDateTime().isAfter(dateProvider.getCurrentDateTime()))
                .sorted(Comparator.comparing(Match::startDateTime)
                        .thenComparing(match -> match.homeTeam().name())
                        .thenComparing(match -> match.awayTeam().name()))
                .limit(howMany)
                .map(match -> (IncomingMatch) match)
                .collect(Collectors.toList());
    }

    private Match createNewIncomingMatch(IncomingMatchCreated incomingMatchCreated) {
        return new IncomingMatch(
                new MatchInformation(
                        incomingMatchCreated.matchId(),
                        incomingMatchCreated.startDateTime(),
                        incomingMatchCreated.homeTeam(),
                        incomingMatchCreated.awayTeam()),
                bettingAllowedBeforeMatchStartOnly(dateProvider),
                defaultRewards());
    }

    private Match finishMatch(MatchFinished matchFinished) {
        return getIncomingMatchBy(matchFinished.matchId()).handle(matchFinished);
    }
}
