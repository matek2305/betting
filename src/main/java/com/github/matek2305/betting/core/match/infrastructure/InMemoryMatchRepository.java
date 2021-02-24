package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.match.domain.FinishedMatch;
import com.github.matek2305.betting.core.match.domain.FinishedMatches;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.Match;
import com.github.matek2305.betting.core.match.domain.MatchEvent;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchResultCorrected;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchNotFoundException;
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Maps.filterKeys;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static java.util.function.Function.identity;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class InMemoryMatchRepository implements MatchRepository, IncomingMatches, FinishedMatches {

    private static final Comparator<Match> BY_START_DATE_TIME_FROM_CLOSEST =
            Comparator.comparing(Match::startDateTime)
                    .thenComparing(match -> match.homeTeam().name())
                    .thenComparing(match -> match.awayTeam().name());


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
    public Set<Match> findBy(Set<MatchId> matchIds) {
        return copyOf(filterKeys(matches, matchIds::contains).values());
    }

    @Override
    public void publish(MatchEvent event) {
        API.Match(event).option(

                Case($(instanceOf(MatchFinished.class)), this::finishMatch),
                Case($(instanceOf(MatchResultCorrected.class)), this::correctMatchResult)

        ).forEach(match -> matches.put(match.matchId(), match));

        publisher.publish("matches", event);
    }

    @Override
    public void save(IncomingMatch match) {
        matches.put(match.matchId(), match);
    }

    @Override
    public IncomingMatch getIncomingMatchBy(MatchId matchId) {
        return API.Match(findBy(matchId)).of(
                Case($Some($(instanceOf(IncomingMatch.class))), identity()),
                Case($(), () -> {
                    throw new MatchNotFoundException(matchId);
                }));
    }

    @Override
    public FinishedMatch getFinishedMatchBy(MatchId matchId) {
        return API.Match(findBy(matchId)).of(
                Case($Some($(instanceOf(FinishedMatch.class))), identity()),
                Case($(), () -> {
                    throw new MatchNotFoundException(matchId);
                }));

    }

    @Override
    public List<IncomingMatch> findNext(int howMany) {
        return findBy(started().negate(), howMany);
    }

    @Override
    public List<IncomingMatch> findStarted(int howMany) {
        return findBy(started(), howMany);
    }

    private List<IncomingMatch> findBy(Predicate<Match> predicate, int howMany) {
        return matches.values()
                .stream()
                .filter(predicate.and(match -> IncomingMatch.class.isAssignableFrom(match.getClass())))
                .sorted(BY_START_DATE_TIME_FROM_CLOSEST)
                .limit(howMany)
                .map(match -> (IncomingMatch) match)
                .collect(Collectors.toList());
    }

    private Predicate<Match> started() {
        return match -> dateProvider.getCurrentDateTime().isAfter(match.startDateTime());
    }

    private FinishedMatch finishMatch(MatchFinished matchFinished) {
        return getIncomingMatchBy(matchFinished.matchId()).handle(matchFinished);
    }

    private FinishedMatch correctMatchResult(MatchResultCorrected resultCorrected) {
        return getFinishedMatchBy(resultCorrected.matchId()).handle(resultCorrected);
    }
}
