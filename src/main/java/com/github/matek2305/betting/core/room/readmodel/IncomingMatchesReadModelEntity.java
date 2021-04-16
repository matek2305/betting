package com.github.matek2305.betting.core.room.readmodel;

import com.github.matek2305.betting.core.match.domain.MatchScore;
import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import lombok.Data;
import lombok.Value;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@TypeDef(name = JsonTypes.JSON, typeClass = JsonType.class)
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class IncomingMatchesReadModelEntity {

    @Id
    private UUID matchId;
    private String homeTeamName;
    private String awayTeamName;
    @Column(name = "start_date_time")
    private ZonedDateTime when;

    @Type(type = JsonTypes.JSON_BIN)
    @Column(columnDefinition = JsonTypes.JSON_BIN)
    private List<Bet> bets = new ArrayList<>();

    @Value
    public static class Bet {
        String playerId;
        MatchScore score;
    }
}