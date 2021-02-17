package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.filterKeys;

@Value
@Getter(AccessLevel.NONE)
@RequiredArgsConstructor
public class PlayerBets {
    Map<MatchId, MatchScore> bets;

    public PlayerBets() {
        this(Collections.emptyMap());
    }

    boolean exist(MatchId matchId) {
        return bets.containsKey(matchId);
    }

    MatchScore get(MatchId matchId) {
        return Option.of(bets.get(matchId))
                .getOrElseThrow(() -> new IllegalArgumentException("Bet not found for match=" + matchId));
    }

    PlayerBets with(MatchId matchId, MatchScore bet) {
        return new PlayerBets(
                new ImmutableMap.Builder<MatchId, MatchScore>()
                        .putAll(filterKeys(bets, id -> !id.equals(matchId)))
                        .put(matchId, bet)
                        .build());
    }

    PlayerBets without(MatchId matchId) {
        return new PlayerBets(
                bets.entrySet()
                        .stream()
                        .filter(entry -> !entry.getKey().equals(matchId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
