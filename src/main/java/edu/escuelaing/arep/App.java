package edu.escuelaing.arep;

import edu.escuelaing.arep.server.HttpServer;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException {
        HttpServer serv = new HttpServer();
        serv.start();
    }
}
