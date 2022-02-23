package edu.escuelaing.arep.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


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

    private JSONObject consulta (String city) throws IOException {
        //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=d4a287f4fdd9cfea36f8265c35be577e");
        //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return readJsonFromUrl("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=d4a287f4fdd9cfea36f8265c35be577e");
    }

    public JSONObject readJsonFromUrl(String link) throws IOException, JSONException {
        InputStream input = new URL(link).openStream();
        // Input Stream Object To Start Streaming.
        try {                                 // try catch for checked exception
            BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
            // Buffer Reading In UTF-8
            String Text = Read(re);         // Handy Method To Read Data From BufferReader
            JSONObject json = new JSONObject(Text);    //Creating A JSON
            return json;    // Returning JSON
        } catch (Exception e) {
            return null;
        } finally {
            input.close();
        }
    }

    public String Read(Reader re) throws IOException {     // class Declaration
        StringBuilder str = new StringBuilder();     // To Store Url Data In String.
        int temp;
        do {

            temp = re.read();       //reading Charcter By Chracter.
            str.append((char) temp);

        } while (temp != -1);
        //  re.read() return -1 when there is end of buffer , data or end of file.

        return str.toString();

    }

}