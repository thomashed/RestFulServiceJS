package app;

import java.io.IOException;
import server.WebService;

/**
 *
 * @author hsty
 */

public class Program
{
    public static void main(String[] args) throws IOException
    {
        WebService service = new WebService(4000);
        service.start();
        
                
        
        
    }
 
}
