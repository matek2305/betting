package com.github.matek2305.betting.core.match.infrastructure.persistence;

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
import com.github.matek2305.betting.core.match.domain.NewMatch;
import com.github.matek2305.betting.core.match.domain.external.ExternalMatch;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.github.matek2305.betting.core.room.domain.IncomingMatches;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

@ApplicationScoped
@RequiredArgsConstructor
class PanacheMatchRepository
        implements PanacheRepository<MatchEntity>, MatchRepository, IncomingMatches, FinishedMatches {

    private final DomainMatchFactory factory;
    private final EventsPublisher publisher;
    private final DateProvider dateProvider;

    @Override
    public Option<Match> findBy(MatchId matchId) {
        var matchEntity = find("uuid", matchId.id()).firstResultOptional();
        return Option.ofOptional(matchEntity).map(factory::create);
    }

    @Override
    public Set<Match> findBy(Set<MatchId> matchIds) {
        var uuids = matchIds.stream().map(MatchId::id).collect(Collectors.toSet());
        return list("uuid in ?1", uuids)
                .stream()
                .map(factory::create)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void publish(MatchEvent event) {
        API.Match(event).option(

                Case($(instanceOf(MatchFinished.class)), this::finishMatch),
                Case($(instanceOf(MatchResultCorrected.class)), this::correctMatchResult)

        );

        publisher.publish("matches", event);
    }

    private Void finishMatch(MatchFinished matchFinished) {
        var entity = getEntityBy(matchFinished.matchId());
        entity.finished = true;
        entity.homeTeamScore = matchFinished.result().homeTeam();
        entity.awayTeamScore = matchFinished.result().awayTeam();
        return run(() -> persist(entity));
    }

    private Void correctMatchResult(MatchResultCorrected resultCorrected) {
        var entity = getEntityBy(resultCorrected.matchId());
        entity.homeTeamScore = resultCorrected.result().homeTeam();
        entity.awayTeamScore = resultCorrected.result().awayTeam();
        return run(() -> persist(entity));
    }

    @Override
    public FinishedMatch getFinishedMatchBy(MatchId matchId) {
        return getBy(matchId, FinishedMatch.class);
    }

    @Override
    public Set<FinishedMatch> getFinishedMatchesBy(Set<MatchId> matchIds) {
        var uuids = matchIds.stream().map(MatchId::id).collect(Collectors.toSet());
        return list("uuid in ?1 AND finished = true", uuids)
                .stream()
                .map(factory::createFinishedMatch)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void publish(AddIncomingMatchEvent event) {
        API.Match(event).option(

                Case($(instanceOf(IncomingMatchAdded.class)),
                        added -> run(() -> save(added.match())))

        );

        publisher.publish("new_matches", event);
    }

    @Override
    public IncomingMatch getIncomingMatchBy(MatchId matchId) {
        return getBy(matchId, IncomingMatch.class);
    }

    @Override
    public List<Match> findNext(int howMany) {
        return list("startDateTime > ?1 order by startDateTime", dateProvider.getCurrentDateTime())
                .stream()
                .map(factory::create)
                .collect(Collectors.toList());
    }

    @Override
    public List<Match> findStarted(int howMany) {
        return list("startDateTime < ?1 and finished = false order by startDateTime", dateProvider.getCurrentDateTime())
                .stream()
                .map(factory::create)
                .collect(Collectors.toList());
    }

    private MatchEntity getEntityBy(MatchId matchId) {
        return find("uuid", matchId.id()).firstResultOptional()
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    private void save(NewMatch match) {
        var matchEntity = API.Match(match).of(
                Case($(instanceOf(IncomingMatch.class)), this::createMatchEntity),
                Case($(instanceOf(ExternalMatch.class)), externalMatch -> createMatchEntity(externalMatch.match()))
        );

        persist(matchEntity);

        API.Match(match).option(

                Case($(instanceOf(ExternalMatch.class)), externalMatch -> {
                    var externalMatchEntity = new ExternalMatchEntity();
                    externalMatchEntity.matchEntity = matchEntity;
                    externalMatchEntity.origin = externalMatch.origin().name();
                    externalMatchEntity.externalId = externalMatch.externalId().id();
                    return externalMatchEntity;
                })

        )
                .forEach(externalMatchEntity -> externalMatchEntity.persist());
    }

    private MatchEntity createMatchEntity(IncomingMatch match) {
        var matchEntity = new MatchEntity();
        matchEntity.uuid = match.matchId().id();
        matchEntity.homeTeamName = match.homeTeamName();
        matchEntity.awayTeamName = match.awayTeamName();
        matchEntity.startDateTime = match.startDateTime();
        matchEntity.finished = false;
        return matchEntity;
    }
}
