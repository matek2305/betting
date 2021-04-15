package com.github.matek2305.betting.core.room.readmodel;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Map;

@Value
public class IncomingMatchesReadModelEntry {
    MatchId matchId;
    Team homeTeam;
    Team awayTeam;
    ZonedDateTime when;
    Map<PlayerId, MatchScore> betsByPlayerId;
}
