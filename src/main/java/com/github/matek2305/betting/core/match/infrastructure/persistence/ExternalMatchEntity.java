package com.github.matek2305.betting.core.match.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
class ExternalMatchEntity extends PanacheEntity {

    @OneToOne
    public MatchEntity matchEntity;

    public String origin;
    public String externalId;
}
