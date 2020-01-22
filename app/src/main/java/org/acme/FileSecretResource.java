package org.acme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/file-secret")
public class FileSecretResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Secret From File /secrets/file-secret :  " + readFile("/secrets/file-secret");
    }

    private String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            return null;
        }
        return contentBuilder.toString();
    }
}