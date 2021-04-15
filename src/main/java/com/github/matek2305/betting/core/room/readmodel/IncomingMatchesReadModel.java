package com.github.matek2305.betting.core.room.readmodel;

import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import com.google.common.collect.Iterables;
import io.quarkus.vertx.ConsumeEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

@ApplicationScoped
@RequiredArgsConstructor
public class IncomingMatchesReadModel {

    private final Set<IncomingMatchesReadModelEntry> matches =
            new TreeSet<>(Comparator.comparing(IncomingMatchesReadModelEntry::when));

    public Iterable<IncomingMatchesReadModelEntry> findNext(int howMany) {
        return Iterables.limit(matches, howMany);
    }

    @ConsumeEvent(value = "new_matches")
    public void handle(IncomingMatchAdded matchAdded) {
        matches.add(new IncomingMatchesReadModelEntry(
                matchAdded.match().matchId(),
                matchAdded.match().matchInformation().homeTeam(),
                matchAdded.match().matchInformation().awayTeam(),
                matchAdded.match().startDateTime(),
                new HashMap<>()
        ));
    }

    @ConsumeEvent(value = "players")
    public void handle(PlayerBetMade betMade) {
        matches.stream()
                .filter(entry -> entry.matchId().equals(betMade.matchId()))
                .forEach(entry -> entry.betsByPlayerId().put(betMade.playerId(), betMade.bet()));
    }
}
