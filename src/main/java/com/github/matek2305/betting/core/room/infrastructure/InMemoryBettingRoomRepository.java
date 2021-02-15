package com.github.matek2305.betting.core.room.infrastructure;

import com.github.matek2305.betting.core.room.domain.BettingRoom;
import com.github.matek2305.betting.core.room.domain.BettingRoomRepository;
import com.github.matek2305.betting.date.DateProvider;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

import static com.github.matek2305.betting.core.room.domain.BettingRoomPolicy.atLeastOneHourBeforeMatchStart;

@ApplicationScoped
@RequiredArgsConstructor
public class InMemoryBettingRoomRepository implements BettingRoomRepository {

    private final DateProvider dateProvider;

    @Override
    public BettingRoom getGlobal() {
        return new BettingRoom(atLeastOneHourBeforeMatchStart(dateProvider));
    }
}
