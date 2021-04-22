package com.github.matek2305.betting.core.room.readmodel;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PointsRewarded;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.github.matek2305.betting.core.room.readmodel.MatchesReadModelEntity.Bet;
import io.quarkus.vertx.ConsumeEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class MatchesReadModel {

    private final EntityManager entityManager;
    private final DateProvider dateProvider;

    public List<MatchesReadModelEntity> findForBetting(int howMany) {
        return entityManager
                .createQuery("" +
                        "select m " +
                        "from MatchesReadModelEntity m " +
                        "where :currentDate < m.bettingAvailableUntil and m.finished = false " +
                        "order by m.when", MatchesReadModelEntity.class)
                .setParameter("currentDate", dateProvider.getCurrentDateTime())
                .setMaxResults(howMany)
                .getResultList();
    }

    public List<MatchesReadModelEntity> findStarted(int howMany) {
        return entityManager
                .createQuery("" +
                        "select m " +
                        "from MatchesReadModelEntity m " +
                        "where :currentDate > m.bettingAvailableUntil " +
                        "order by m.when", MatchesReadModelEntity.class)
                .setParameter("currentDate", dateProvider.getCurrentDateTime())
                .setMaxResults(howMany)
                .getResultList();
    }

    @Transactional
    @ConsumeEvent(value = IncomingMatchAdded.ADDRESS, blocking = true)
    public void handle(IncomingMatchAdded matchAdded) {
        var entity = new MatchesReadModelEntity();
        entity.matchId(matchAdded.match().matchId().id());
        entity.homeTeamName(matchAdded.match().homeTeamName());
        entity.awayTeamName(matchAdded.match().awayTeamName());
        entity.when(matchAdded.match().startDateTime());
        entity.bettingAvailableUntil(matchAdded.match().bettingAvailableUntil());
        entityManager.persist(entity);
    }

    @Transactional
    @ConsumeEvent(value = PlayerBetMade.ADDRESS, blocking = true)
    public void handle(PlayerBetMade betMade) {
        var entity = entityManager.find(MatchesReadModelEntity.class, betMade.matchId().id());
        entity.bets().removeIf(bet -> bet.playerId().equals(betMade.playerId().id()));
        entity.bets().add(Bet.pending(betMade.playerId().id(), betMade.bet()));
        entityManager.persist(entity);
    }

    @Transactional
    @ConsumeEvent(value = MatchFinished.ADDRESS, blocking = true)
    public void handle(MatchFinished matchFinished) {
        var entity = entityManager.find(MatchesReadModelEntity.class, matchFinished.matchId().id());
        entity.finished(true);
        entity.result(matchFinished.result());
        entityManager.persist(entity);
    }

    @Transactional
    @ConsumeEvent(value = PointsRewarded.ADDRESS, blocking = true)
    public void handle(PointsRewarded pointsRewarded) {
        var entity = entityManager.find(MatchesReadModelEntity.class, pointsRewarded.matchId().id());
        var bets = entity.bets()
                .stream()
                .map(bet -> bet.playerId().equals(pointsRewarded.playerId().id())
                        ? bet.withReceivedPoints(pointsRewarded.points().points())
                        : bet
                ).collect(Collectors.toList());
        entity.bets(bets);
        entityManager.persist(entity);
    }
}
