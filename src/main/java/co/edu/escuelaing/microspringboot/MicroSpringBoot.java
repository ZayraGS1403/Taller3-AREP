

package co.edu.escuelaing.microspringboot;

import co.edu.escuelaing.httpserver.HttpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class MicroSpringBoot {
    public static void main(String[] args) throws IOException, URISyntaxException, IllegalAccessException {
        System.out.println("Start micro spring boot");
        HttpServer.startServer(args);
    }
}
