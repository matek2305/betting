package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.MatchRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AddIncomingMatch {

    private final BettingRoomRepository bettingRoomRepository;
    private final MatchRepository matchRepository;

    public Try<Void> add(AddIncomingMatchCommand command) {
        return Try.run(() -> {
            var globalRoom = bettingRoomRepository.getGlobal();
            matchRepository.publish(globalRoom.add(command));
        }).onFailure(cause -> log.debug("Add incoming match command rejected: {}", command));
    }
}
