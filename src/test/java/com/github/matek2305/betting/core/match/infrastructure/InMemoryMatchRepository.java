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
import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

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
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class InMemoryMatchRepository implements MatchRepository, IncomingMatches, FinishedMatches {

    private static final Comparator<Match> BY_START_DATE_TIME_FROM_CLOSEST =
            Comparator
                    .comparing(Match::startDateTime)
                    .thenComparing(Match::homeTeamName)
                    .thenComparing(Match::awayTeamName);


    private final Map<MatchId, Match> matches = new ConcurrentHashMap<>();

    private final EventsPublisher publisher;
    private final DateProvider dateProvider;

    @Override
    public Option<Match> findBy(MatchId matchId) {
        return Option.of(matches.get(matchId));
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

        )
                .forEach(match -> matches.put(match.matchId(), match));

        publisher.publish(event);
    }

    @Override
    public void publish(AddIncomingMatchEvent event) {
        API.Match(event).option(

                Case($(instanceOf(IncomingMatchAdded.class)), IncomingMatchAdded::match)

        )
                .forEach(newMatch -> matches.put(newMatch.matchId(), newMatch));

        publisher.publish(event);
    }

    @Override
    public IncomingMatch getIncomingMatchBy(MatchId matchId) {
        return getBy(matchId, IncomingMatch.class);
    }

    @Override
    public FinishedMatch getFinishedMatchBy(MatchId matchId) {
        return getBy(matchId, FinishedMatch.class);
    }

    @Override
    public Set<FinishedMatch> getFinishedMatchesBy(Set<MatchId> matchIds) {
        return matches.values()
                .stream()
                .filter(match -> matchIds.contains(match.matchId()))
                .filter(instanceOf(FinishedMatch.class))
                .map(match -> (FinishedMatch) match)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Match> findNext(int howMany) {
        return findBy(notStarted(), howMany);
    }

    private List<Match> findBy(Predicate<Match> filter, int howMany) {
        return matches.values()
                .stream()
                .filter(filter)
                .sorted(BY_START_DATE_TIME_FROM_CLOSEST)
                .limit(howMany)
                .collect(Collectors.toList());
    }

    private Predicate<Match> notStarted() {
        return match -> dateProvider.getCurrentDateTime().isBefore(match.startDateTime());
    }

    private FinishedMatch finishMatch(MatchFinished matchFinished) {
        var match = getBy(matchFinished.matchId(), IncomingMatch.class);
        return new FinishedMatch(
                match.matchInformation(),
                matchFinished.result(),
                match.rewardingPolicy()
        );
    }

    private FinishedMatch correctMatchResult(MatchResultCorrected resultCorrected) {
        var match = getBy(resultCorrected.matchId(), FinishedMatch.class);
        return new FinishedMatch(
                match.matchInformation(),
                resultCorrected.result(),
                match.rewardingPolicy()
        );
    }
}
