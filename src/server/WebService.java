package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Person;

/**
 *
 * @author hsty
 */
public class WebService
{

    private final HttpServer server;

    List<Person> persons = new ArrayList<>();

    public WebService(int port) throws IOException
    {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        server.createContext("/connect", createHandler());
        server.createContext("/helloworld", createHelleWorldHandler());
        server.createContext("/postdata", createPostHandler());
        server.createContext("/person", createPersonHandler());
        server.createContext("/files", createFileHandler());

        persons.add(new Person(1, "Peter", 21));
        persons.add(new Person(2, "Jim", 25));
        persons.add(new Person(3, "Helle", 19));
    }

    private HttpHandler createHandler()
    {
        return new HttpHandler()
        {
            @Override
            public void handle(HttpExchange he) throws IOException
            {
                he.sendResponseHeaders(200, 0);
            }
        };
    }

    public void start()
    {
        server.start();
        System.err.println("Server startet: " + server.getAddress().toString());
    }

    public void stop()
    {
        server.stop(0);
    }

    private HttpHandler createHelleWorldHandler()
    {
        return new HttpHandler()
        {
            @Override
            public void handle(HttpExchange he) throws IOException
            {
                he.sendResponseHeaders(200, 0);
                try (OutputStream responseBody = he.getResponseBody())
                {
                    responseBody.write("hello world".getBytes());
                }
            }
        };
    }

    private HttpHandler createPostHandler()
    {
        return new HttpHandler()
        {
            @Override
            public void handle(HttpExchange he) throws IOException
            {

                String message = "";
                Scanner scanner = new Scanner(he.getRequestBody());
                while (scanner.hasNextLine())
                {
                    message += scanner.nextLine();
                }

                he.sendResponseHeaders(200, 0);
                try (OutputStream responseBody = he.getResponseBody())
                {
                    responseBody.write(message.getBytes());
                }
            }
        };
    }

    private HttpHandler createPersonHandler()
    {
        return new HttpHandler()
        {
            @Override
            public void handle(HttpExchange he) throws IOException
            {
                String path = he.getRequestURI().getPath();
                int lastIndexOf = path.lastIndexOf("/");

                if (lastIndexOf > 0)
                {
                    int id = Integer.parseInt(path.substring(lastIndexOf + 1));

                                       
                    Optional<Person> person = persons.stream()
                            .filter(p -> p.getId() == id)
                            .findFirst();

                    if (person.isPresent())
                    {
                        if("DELETE".equalsIgnoreCase(he.getRequestMethod()))
                        {
                            persons.remove(person.get());
                            send(he, 200, new Gson().toJson(persons));
                        }
                        else {
                            String json = new Gson().toJson(person.get());
                            send(he, 200, json);
                        }
                    } else
                    {
                        String error = "Person " + id + " not found";
                        error(he, 404, error);
                    }

                    return;
                }

                String message = "";
                Scanner scanner = new Scanner(he.getRequestBody());
                while (scanner.hasNextLine())
                {
                    message += scanner.nextLine();
                }
                
                if("POST".equalsIgnoreCase(he.getRequestMethod()))
                {
                    Person fromJson 
                            = new Gson().fromJson(message, Person.class);
                    fromJson.setId(persons.size() + 1);
                    persons.add(fromJson);
                }

                he.sendResponseHeaders(200, 0);
                try (OutputStream responseBody = he.getResponseBody())
                {
                    String json = new Gson().toJson(persons);
                    responseBody.write(json.getBytes());
                }
            }
        };
    }

    private HttpHandler createFileHandler()
    {
        return new HttpHandler()
        {
            @Override
            public void handle(HttpExchange he) throws IOException
            {
                
                Pattern pattern = Pattern.compile("/files/(.+)$");
                Matcher matcher = 
                        pattern.matcher(he.getRequestURI().getPath());
                if(!matcher.matches())
                {
                    error(he, 404, "No file defined");
                    return;
                }
                
                String fileName = matcher.group(1);
                
                File file = new File(fileName);

                if (!file.exists())
                {
                    error(he, 404, "File not found");
                    return;
                }
                he.sendResponseHeaders(200, 0);
                FileInputStream in = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                try (OutputStream responseBody = he.getResponseBody())
                {
                    while(true)
                    {
                        int bytesRead = in.read(buffer, 0, 4096);
                        if(bytesRead <= 0)
                        {
                            break;
                        }
                        responseBody.write(buffer, 0, bytesRead);
                    }
                }
            }

        };
    }

    private void error(HttpExchange he, int statusCode, String message) throws IOException
    {
        send(he, statusCode, message);
    }

    private void send(HttpExchange he, int statusCode, String message) throws IOException
    {
        he.sendResponseHeaders(statusCode, 0);
        try (OutputStream responseBody = he.getResponseBody())
        {
            responseBody.write(message.getBytes());
        }
    }
}
