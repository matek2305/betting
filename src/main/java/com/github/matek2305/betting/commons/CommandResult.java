package com.github.matek2305.betting.commons;

import lombok.Value;

public interface CommandResult {

    static CommandResult allowed() {
        return new Allowed();
    }

    static CommandResult rejected(String reason) {
        return new Rejected(reason);
    }

    class Allowed implements CommandResult {
    }

    @Value
    class Rejected implements CommandResult {
        String rejectionReason;
    }
}
