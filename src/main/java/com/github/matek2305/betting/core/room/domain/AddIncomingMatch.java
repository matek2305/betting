package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.AddIncomingMatchRejected;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchEvent.IncomingMatchAdded;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class AddIncomingMatch {

    private final BettingRoomRepository bettingRoomRepository;
    private final IncomingMatches incomingMatches;

    public CommandResult add(AddExternalMatchCommand command) {
        var globalRoom = bettingRoomRepository.getGlobal();
        return API.Match(globalRoom.add(command)).of(
                Case($Right($()), this::publish),
                Case($Left($()), this::publish)
        );
    }

    public CommandResult add(AddIncomingMatchCommand command) {
        var globalRoom = bettingRoomRepository.getGlobal();
        return API.Match(globalRoom.add(command)).of(
                Case($Right($()), this::publish),
                Case($Left($()), this::publish)
        );
    }

    private CommandResult publish(IncomingMatchAdded added) {
        incomingMatches.publish(added);
        return CommandResult.allowed();
    }

    private CommandResult publish(AddIncomingMatchRejected rejected) {
        incomingMatches.publish(rejected);
        return CommandResult.rejected(rejected.rejectionReason());
    }
}
