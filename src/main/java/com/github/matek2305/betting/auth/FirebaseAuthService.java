package com.github.matek2305.betting.auth;

import lombok.Value;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface FirebaseAuthService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/accounts:signInWithPassword")
    Response signInWithPassword(SignInWithPasswordRequest request, @QueryParam("key") String apiKey);

    @Value
    class SignInWithPasswordRequest {
        String email;
        String password;
        boolean returnSecureToken = true;
    }
}
