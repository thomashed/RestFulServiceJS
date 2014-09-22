package acceptance;

import server.WebService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class WebServiceAcceptanceTest
{
    final int port = 3000;
    final String host = "http://localhost:" + port;
    private WebService service;
    
    @Before
    public void before() throws IOException
    {
        service = new WebService(port);
        service.start();
    }
    
    @After
    public void after()
    {
        service.stop();
    }
        
        
    @Test
    public void connectionReturnsCorrectStatusCode() throws MalformedURLException, IOException, InterruptedException
    {
         HttpURLConnection client
                = (HttpURLConnection) new URL(host + "/connect").openConnection();

        int responseCode = client.getResponseCode();

        assertEquals(200, responseCode);
    }

    @Test
    public void connectionReturnsCorrectData() throws MalformedURLException, IOException, InterruptedException
    {
        HttpURLConnection client
                = (HttpURLConnection) new URL(host + "/helloworld").openConnection();
        
        String message = "";
        Scanner scanner = new Scanner(client.getInputStream());
        while (scanner.hasNextLine())
        {
            message += scanner.nextLine();
        }

        assertEquals("hello world", message);
    }

    @Test
    public void postReturnsCorrectData() throws MalformedURLException, IOException, InterruptedException
    {
        HttpURLConnection client
                = (HttpURLConnection) new URL(host + "/postdata").openConnection();

        String sendMessage = "hey";
        client.setDoOutput(true);
        try(OutputStream outputStream = client.getOutputStream())
        {
            outputStream.write(sendMessage.getBytes());
        }

        String message = "";
        Scanner scanner = new Scanner(client.getInputStream());
        while (scanner.hasNextLine())
        {
            message += scanner.nextLine();
        }

        assertEquals(sendMessage, message);
    }
}
