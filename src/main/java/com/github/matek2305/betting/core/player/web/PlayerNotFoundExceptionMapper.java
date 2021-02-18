package com.github.matek2305.betting.core.player.web;

import com.github.matek2305.betting.core.player.domain.PlayerNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PlayerNotFoundExceptionMapper implements ExceptionMapper<PlayerNotFoundException> {

    @Override
    public Response toResponse(PlayerNotFoundException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(exception.getLocalizedMessage())
                .build();
    }
}
