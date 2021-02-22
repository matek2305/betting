package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.commons.CommandResult;
import com.github.matek2305.betting.core.match.domain.IncomingMatch;
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

    public CommandResult add(AddIncomingMatchCommand command) {
        var globalRoom = bettingRoomRepository.getGlobal();
        return API.Match(globalRoom.add(command)).of(
                Case($Right($()), this::save),
                Case($Left($()), this::rejected)
        );
    }

    private CommandResult save(IncomingMatch match) {
        incomingMatches.save(match);
        return CommandResult.allowed();
    }

    private CommandResult rejected(AddIncomingMatchRejected rejected) {
        return CommandResult.rejected(rejected.rejectionReason());
    }
}
