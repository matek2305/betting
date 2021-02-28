package com.github.matek2305.betting.core.match.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Version;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
class MatchEntity extends PanacheEntity {

    public UUID uuid;
    public String homeTeamName;
    public String awayTeamName;
    public ZonedDateTime startDateTime;
    public boolean finished;
    public int homeTeamScore;
    public int awayTeamScore;

    @CreationTimestamp
    public ZonedDateTime createdAt;

    @UpdateTimestamp
    public ZonedDateTime updatedAt;

    @Version
    public long version;
}
