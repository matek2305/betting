package com.github.matek2305.betting.core.room.readmodel;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.github.matek2305.betting.core.room.readmodel.IncomingMatchesReadModelEntity.Bet;
import io.quarkus.vertx.ConsumeEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class IncomingMatchesReadModel {

    private final EntityManager entityManager;

    public List<IncomingMatchesReadModelEntity> findNext(int howMany) {
        return entityManager
                .createQuery("select m from IncomingMatchesReadModelEntity m order by m.when", IncomingMatchesReadModelEntity.class)
                .setMaxResults(howMany)
                .getResultList();
    }

    @Transactional
    @ConsumeEvent(value = "new_matches", blocking = true)
    public void handle(IncomingMatchAdded matchAdded) {
        var entity = new IncomingMatchesReadModelEntity();
        entity.matchId(matchAdded.match().matchId().id());
        entity.homeTeamName(matchAdded.match().homeTeamName());
        entity.awayTeamName(matchAdded.match().awayTeamName());
        entity.when(matchAdded.match().startDateTime());
        entityManager.persist(entity);
    }

    @Transactional
    @ConsumeEvent(value = "players", blocking = true)
    public void handle(PlayerBetMade betMade) {
        var entity = entityManager.find(IncomingMatchesReadModelEntity.class, betMade.matchId().id());
        entity.bets().removeIf(bet -> bet.playerId().equals(betMade.playerId().id()));
        entity.bets().add(new Bet(betMade.playerId().id(), betMade.bet()));
        entityManager.persist(entity);
    }

    @Transactional
    @ConsumeEvent(value = "matches", blocking = true)
    public void handle(MatchFinished matchFinished) {
        entityManager
                .createQuery("delete from IncomingMatchesReadModelEntity m where m.matchId = :matchId")
                .setParameter("matchId", matchFinished.matchId())
                .executeUpdate();
    }
}
