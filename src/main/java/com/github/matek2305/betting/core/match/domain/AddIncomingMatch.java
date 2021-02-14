package com.github.matek2305.betting.core.match.domain;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddIncomingMatch {

    private final BettingRooms bettingRooms;
    private final MatchRepository repository;

    public Try<Void> add(AddIncomingMatchCommand command) {
        return Try.run(() -> {
            var globalRoom = bettingRooms.getGlobal();
            repository.publish(globalRoom.add(command));
        });
    }
}
