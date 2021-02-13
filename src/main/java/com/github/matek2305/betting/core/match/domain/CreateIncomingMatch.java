package com.github.matek2305.betting.core.match.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateIncomingMatch {

    private final CreateIncomingMatchPolicy createIncomingMatchPolicy;
    private final MatchRepository repository;

    public void create(CreateIncomingMatchCommand command) {
        if (createIncomingMatchPolicy.canBeCreated(command)) {
            repository.publish(command.accept());
        } else {
            repository.publish(command.reject());
        }
    }
}
