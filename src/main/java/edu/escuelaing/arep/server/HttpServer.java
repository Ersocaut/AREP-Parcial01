package edu.escuelaing.arep.server;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    private String defaultPage = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n"
            + "<!DOCTYPE html>"
            + "<html>"
            + "<head>"
            + "<meta charset=\"UTF-8\">"
            + "<title>Title of the document</title>\n"
            + "</head>"
            + "<body>"
            + "Servicio por defecto funcionando"
            + "</body>"
            + "</html>";

    public static OutputStream outputStream;

    public final static Map<String, String> types = new HashMap<String, String>();

    public HttpServer(){
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }


    public void start() throws IOException, URISyntaxException {

        serverSocket = null;

        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;

        while (running) {
            clientSocket = null;

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;
            boolean primeraLinea = true;
            String file = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (primeraLinea){
                    file = inputLine.split(" ")[1];
                    primeraLinea = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (file.startsWith("/clima")){
                String value;
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Title of the document</title>\n"
                        + "</head>"
                        + "<body>"
                        + "<input>"
                        + "</input>"
                        + "<button>"
                        + "Obtener clima"
                        + "</button>"
                        + "</body>"
                        + "</html>";

            }else if (file.startsWith("/consulta")){

                System.out.println("ciudad: " + file.split("=")[1]);

                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + consulta(file.split("=")[1]);

            }else{
                outputLine = defaultPage;
            }
                out.println(outputLine);


            closeConnection();
        }
        serverSocket.close();
    }

    private void closeConnection(){
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String consulta (String city) throws IOException {
        String ret = "";
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=d4a287f4fdd9cfea36f8265c35be577e");
        URLConnection urlConnection = url.openConnection();

        Map<String, List<String>> headers = urlConnection.getHeaderFields();

        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String headerName = entry.getKey();
            if(headerName !=null){System.out.print(headerName + ":");}
            List<String> headerValues = entry.getValue();
            for (String value : headerValues) {
                System.out.print(value);
            }
            System.out.println("");
        }

        System.out.println("-------message-body------");
        BufferedReader reader =
                new BufferedReader(new
                        InputStreamReader(urlConnection.getInputStream()));


        System.out.println("___________________");

        try (BufferedReader nreader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine = null;
            while ((inputLine = nreader.readLine()) != null) {
                ret += inputLine;
                System.out.println(inputLine);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return ret;
    }}