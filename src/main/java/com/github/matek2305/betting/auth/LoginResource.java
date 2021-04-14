package com.github.matek2305.betting.auth;

import com.github.matek2305.betting.auth.FirebaseAuthService.SignInWithPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/login")
@RequiredArgsConstructor
public class LoginResource {

    @ConfigProperty(name = "betting.auth.firebase-auth-url")
    String firebaseAuthUrl;

    @ConfigProperty(name = "betting.auth.firebase-auth-domain")
    String firebaseAuthDomain;

    @ConfigProperty(name = "betting.auth.firebase-auth-api-key")
    String firebaseAuthAPIKey;

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) throws URISyntaxException {
        return Response.fromResponse(
                RestClientBuilder.newBuilder()
                        .property("microprofile.rest.client.disable.default.mapper", true)
                        .baseUri(new URI(firebaseAuthUrl))
                        .build(FirebaseAuthService.class)

                        .signInWithPassword(
                                new SignInWithPasswordRequest(
                                        request.username() + "@" + firebaseAuthDomain,
                                        request.password()
                                ),
                                firebaseAuthAPIKey
                        )
        )
                .header("Transfer-Encoding", null)
                .build();
    }

    @Value
    static class LoginRequest {
        String username;
        String password;
    }
}
