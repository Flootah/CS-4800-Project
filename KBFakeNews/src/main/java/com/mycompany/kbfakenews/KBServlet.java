package com.mycompany.kbfakenews;
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.*;

//TEMP IMPORTS
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Eduardo S.
 */
public class KBServlet {
    private KBTest kb;
    private static final long serialVersionUID = 1L;
    
    public KBServlet() {
        //super();
    }
    
    public static void main(String[] args) throws IOException{
        KBServlet server = new KBServlet();
        server.doPost("testjson1.json");
        server.doPost("testjson2.json");
        server.doPost("testjson3.json");
    }
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println();
        }
    }

   /*
    * @Author Eduardo S.
    * So this one is meant for use with an actual POST Request.
    * Will theoretically read a JSON from the request place, and then return with response.
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("KBServlet.doPost()");
        StringBuilder sb = new StringBuilder();         // string maker to make JSON String
        BufferedReader reader = request.getReader();    // reader for POST request
        
        try {                                           // try to build JSON
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        String jsonString = sb.toString();
        System.out.println(jsonString);                 // print interpreted string
        JSONObject json = new JSONObject(jsonString);   // make into JSON Object
        kb = new KBTest(json);
        
    }
    
    protected void doPost(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();         // string maker to make JSON String
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("parsing json: ");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)) // reader from test JSON File
        ) {                                           // try to build JSON
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            
        }
        String jsonString = sb.toString();
        System.out.println(jsonString);                 // print interpreted string
        JSONObject json = new JSONObject(jsonString);   // make into JSON Object
        kb = new KBTest(json);
        
    }
}
