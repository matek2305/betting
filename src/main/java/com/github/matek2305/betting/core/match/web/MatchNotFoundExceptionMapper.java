package com.github.matek2305.betting.core.match.web;

import com.github.matek2305.betting.core.match.domain.MatchNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MatchNotFoundExceptionMapper implements ExceptionMapper<MatchNotFoundException> {

    @Override
    public Response toResponse(MatchNotFoundException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(exception.getLocalizedMessage())
                .build();
    }
}
