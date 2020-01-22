package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/env-secret")
public class EnvironmentSecretResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Secret From ENV BASIC_SECRET_ENV :  " + System.getenv("BASIC_SECRET_ENV");
    }
}