package com.mycompany.kbfakenews;
import java.util.*;
// JSON imports
import java.io.IOException;
import org.json.simple.JSONObject;
// JSoup imports
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLConnection;
import java.net.URL;
        

/**
 *
 * @author Rashad, Ed S., Ammar, Brandon M.
 * 
 * Knowledge-Based Fake News Detector
 **/
public class KBTest {
    // fields
    private JSONObject articleJ;    // article to test
    private String authorJ;         // article author
    private String URLJ;            // article URL
    private String dateJ;           // article Date
    private String contentJ;        // article content
    /*
    * Constructor for this class.
    * currently does nothing.
    */
    public KBTest(JSONObject obj) {
        // initialize variables
        // eventually, we'll be putting code to scan articleJ and fill the respective
        // values here.
        articleJ = obj;
        // TODO JSON recieve code and filling J variables
        authorJ = "June J. Pilcher";
        URLJ = "";
        dateJ = "2010-11-25";
        contentJ = "";
    }
    
    //making connection to net
    public static HttpURLConnection connection;
    
    //calling avarage test results
    //articleJ is the json file
    public static void main(String[] args) throws IOException{
        JSONObject dummy = new JSONObject();
	KBTest tester = new KBTest(dummy);
        System.out.println(tester.resultAVG());
    }
    
    //avg Check
    public float resultAVG() throws IOException{
	//calculating avarage
	float avg = ((webCheck(URLJ) + authorCheck(authorJ) + articleCheck(contentJ) + dateCheck(dateJ))/4);
	return avg;
}

    //website check
    private float webCheck(String url){
       
       return 0;
    }



     /*
     * @author Ed S.
     * 
     * Author trust factor algorithm, based on a search of google scholar's i-10 index.
     * 
     * Takes in a string containing an author name, then uses Google Scholar's API to retrieve
     * an i-10 index for that author. A value between 0 and 1 is returned as a representation of
     * that author's trust factor; 0 being not trustworthy, and 1 being very trustworthy.
     * 
     * throws IOException mainly because the program gets mad if i don't, it has something to
     * do with JSoup.
     */
    private float authorCheck(String author) throws IOException{
	// initialize variables
    	float trust;                    // author's calculated trust
    	int citations = 0;		// citations
    	int r_citations = 0;            // citations since 2015
    	int hindex = 0;			// h-index
    	int r_hindex = 0;		// h-index since 2015
    	int i10index = 0;		// i10-index
    	int r_i10index = 0;		// i10-index since 2015
    	
    	// splitting author's name into first and last names.
    	String[] s = author.split(" ");
    	String first = s[0].toLowerCase();
    	String last = s[s.length-1].toLowerCase();
    	// search google scholar for author
    	Document doc = Jsoup.connect("https://scholar.google.com/citations?view_op=search_authors&hl=en&mauthors=" + author).get();
    	// retrieve all <a> tags to profiles that are in the results page
    	Elements results = doc.select("h3[class=gs_ai_name] > a");
    	
    	boolean found = false;
    	// iterate through all <a> tags
    	for (Element link : results) {
                if (found == true) break;
    		// store name
    		String name = link.text();
    		// store link
    		String address = link.attr("abs:href");
    		// debug: log(name);
    		// debug: log(address);
    		if(name.toLowerCase().contains(first) && name.toLowerCase().contains(last)) {
    			//log("match found, parsing " + name + "'s profile.");
    			doc = Jsoup.connect(address).get();
    			found = true;
    		}
    	}
    	// if no author found, return value of 0.5 for now.
    	// also i have to cast it to float for some dumb reason
    	if(!found) return trust = (float) 0.5;
    	// select table on page that contains the relevant information
    	Elements dataTable = doc.select("table[id=gsc_rsb_st] > tbody > tr");
    	// iterate thorugh rows on the table
    	for (int i = 0; i < dataTable.size(); i++) {
    		// give nice variable name for the current row
    		Element row = dataTable.get(i);
    		// select the "all" and "recent" columns, store.
			String all = row.select("td").get(1).text();
			String recent = row.select("td").get(2).text();
			// based on which row we're looking at, store found values in respective variable
    		switch(i) {
    		case 0:			// citations row
    			citations = Integer.parseInt(all);
    			r_citations = Integer.parseInt(recent);
    			break;
    		case 1:			// h-index row
    			hindex = Integer.parseInt(all);
    			r_hindex = Integer.parseInt(recent);
    			break;
    		case 2:			// i10-index row
    			i10index = Integer.parseInt(all);
    			r_i10index = Integer.parseInt(recent);
    			break;
    		default:		// unknown row...
    			log("yeah something's wrong here...");
    			break;
    		}
    	}
        /* debug prints
    	System.out.println("citations: " + citations);		// citations
    	System.out.println("r_citations: " + r_citations);	// citations since 2015
    	System.out.println("hindex: " + hindex);		// h-index
    	System.out.println("r_hindex: " + r_hindex);		// h-index since 2015
    	System.out.println("i10index: " + i10index);		// i10-index
    	System.out.println("r_i10index: " + r_i10index);	// i10-index since 2015
        */
    	// super top secret complex algorithm at work: 
    	float hvalue = (float) (Math.min((r_hindex/25.0), 1.0) * 0.3);			// hindex scales between 0-25, worth 3/10 of total
    	float cvalue = (float) (Math.min(r_citations/1500.0, 1.0) * 0.2);		// citation scales between 0-1500, worth 1/5 of total
    	float i10value = (float) (Math.min(r_i10index/50.0, 1.0) * 0.2);		// i10index scales between 0-50, worth  of total
    	if(r_hindex > 25.0) {
    	hvalue += (float) (Math.min(((r_hindex-25.0)/25.0), 1.0) * 0.1);                // hindex scales further between 25-50, worth 1/10 of total\
        }
        if(r_citations > 1500.0) {
    	cvalue += (float) (Math.min((r_citations-1500.0)/3500.0, 1.0) * 0.1);           // citation scales further between 1500-5000, worth 1/10 of total
        }
        if(r_i10index > 50.0) {
    	i10value += (float) (Math.min((r_i10index-50.0)/50.0, 1.0) * 0.1);		// i10index scales further between 50-100, worth 1/10 of total
        }
        trust = hvalue + cvalue + i10value;
    	log("final author trust: " + trust);	// i10-index since 2015
    	
    	return trust;
    }
    //returns a value of 0-100 if the preson has has a i-10 index higheer 
    //then a determined ammount that exceeds the avarage

    
    //article check 
    private float articleCheck(String content){
		//kumars:   url     page_data<citation_urls>       page_data<body>
    //Allysas:  url     citation_urls                  content
    

    //IMPORTANT!!!!!!!!!!!
    //I need the citation urls in an ARRAYLIST<STRING>!!!!!!!!!! mine is named     public ArrayList<String> Citation_Urls;
    //I NEED THE ARTICAL URL AND ARTICLE AS A STRING for..... reasons...... :)     mine are named      String Article           and             String JSON_Url = "";

    //Gets URL, makes it lowercase;
    
    ArrayList<String> Nonbias_Citations = new ArrayList<String>();
    ArrayList<String> Citation_Urls = new ArrayList<String>();

    String JSON_Url = "";
    String Main_Url = JSON_Url.toLowerCase();
    
    //gets ArrayList of citation URLs
    
    
        
    //Load body of article into stuff BELOW
    String Article = "";

    //Trust_Me_Citations for citation trust
    //Trust_Me_senpai for body trust......  “(◉◞౪◟◉｀)”
    //Trust for final trust value
    float Trust_Me_Citations;        
    float Trust_Me_Senpai;
    float Trust;
    boolean yes = true;
    String Final_Form_Url = "";
    
    //to tell between Alyssas and Kumars URL
    if (Main_Url.contains("www.") == true)
    {
        String Simple_URL = Main_Url.replaceFirst("http://www", "");
        Final_Form_Url = Simple_URL.substring(0, Simple_URL.indexOf("."));
        
    }
    else
    {
        String Simple_URL = Main_Url.replaceFirst("https://", "");
        Final_Form_Url = Simple_URL.substring(0, Simple_URL.indexOf("."));
    }
    
    //arrayList of domains to check for bias and unreliability
    String[] Social_Media = new String[] {Final_Form_Url,"facebook","twitter","linkedin","reddit","instagram", "myspace"};
    
    
    //If no citations, then nah. You get a 0 from oprah.
    if(Citation_Urls.isEmpty())
    {
        Trust = 0;
    }
    else
    {
        //goes through arraylist of citation urls, 
        for(int i = 0; i < Citation_Urls.size(); i++ )
        {
            String Final_Form_Citation = "";
            //make it lowercase
            String Citation = Citation_Urls.get(i).toLowerCase();
            //again to check who gave the friggin citations
            if(Citation.contains("www."))
            {
                Citation = Citation.replaceFirst("http://www.", "");
                Final_Form_Citation = Citation.substring(0, Citation.indexOf("."));
            }
            else
            {
                Citation = Citation.replaceFirst("https://", "");
                Final_Form_Citation = Citation.substring(0, Citation.indexOf("."));
            }
                //goes through array of known social media and biased cites to filter href citations
                for(int j = 0; j < Social_Media.length; j++)
                {
                    if(Social_Media[j].equals(Final_Form_Citation)) 
                    {                     
                    }
                    else
                    {   
                        Nonbias_Citations.add(Citation_Urls.get(i));
                    }
                }
        }
        //we now have arraylist of GOOD UNBIASED citation.
        //assign that to a value
        int Good_Shit = Nonbias_Citations.size();
        
            //give the value MEANING!!!!!
            switch (Good_Shit) 
			{
                case 0:
                    Trust_Me_Citations = (float) 0.0;
                    break;
                case 1:
                case 2:
                    Trust_Me_Citations = (float) 0.356465468435146843546516841365635434355334145546385443132164515335446535314653654442069;
                    break;
                case 3:
                case 4:
                case 5:
                    Trust_Me_Citations = (float) 0.692335442635343413234623635443541533524356835485394839053556254343545354436573131342069;
                    break;
                default:
                    Trust_Me_Citations = (float) 0.905668546354445834783748578678768787908070897084252221221352686464163168151058456842069;
                    break;
            }
            
            
            

     //now check article length
     //convert string to bytes
     //use an algorithm i made before to count words
        byte[] Article_Byte = Article.getBytes(StandardCharsets.US_ASCII);
        int Words_In_Article = 0;
        for(int i = 0; i < Article.length(); i++)
        {
            if (Article_Byte[i] == 32 || Article_Byte[i] == 46)
            {
                if((i != 0 &&  Article_Byte[i-1] == 46)) 
                {
                    //do nothing 
                }
                else if(i != (Article.length()-1) && ( Article_Byte[i+1] == 46))
                {
                    //do ABSOLUTELY NOTHING :)
                }
                 else
                {
                Words_In_Article += 1;
                }
            }
        }
        
        //Now we give it more math in accordance to word length
        if(Words_In_Article == 0)
        {
            Trust_Me_Senpai = (float) 0.0;
        }
        else if(Words_In_Article > 0 && Words_In_Article < 100 )
        {
            Trust_Me_Senpai = (float) 0.356465468435146843546516841365635434355334145546385443132164515335446535314653654442069;
        }
        else if(Words_In_Article > 100 && Words_In_Article <200  )
        {
            Trust_Me_Senpai = (float) 0.644584454854842184121561356463541321685232122312121112354354684968469843541632106342069;
        }
        else if(Words_In_Article > 200 && Words_In_Article < 500 )
        {
            Trust_Me_Senpai = (float) 0.831646431316494915451744527443556674454543413654651354334435413634551334543568367842069;
        }
        else
        {
            Trust_Me_Senpai = (float) 0.9422215133665545841136396646865343451846262461276216416421967216742121672167451765242069;
        }
        //gotta bring it all together!!!!!!!
        Trust = (float) ((0.75 * Trust_Me_Citations) + (0.25 * Trust_Me_Senpai));
        
    }  
    return Trust;
}
    
    /*returns a value corrisponding to articles credibility.
    if the article has been cited before it adds either 0 to 100 to the output 
    depending on what criteria we have for the ratio of citations.*/

    /*@author Ammar
	Check to see how old the article is. The older the article is the less credibilty it has.
	*/
    private float dateCheck(String date){
		//set variables and pull current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		float returnValue;
		
		//convert string to int and isolate the year
        String d = date;
        int year = Integer.parseInt(d.split("-")[0] );
    
		//if statement to check how recent the article is
		if ((year <= currentYear) && (year > (currentYear-10))){
			returnValue = (float) 1;
		} else if ((year <= (currentYear-10)) && (year > (currentYear-20))){
			returnValue = (float) 0.75;
		} else if (year > currentYear){
			returnValue = (float) 0;
		} else if (d.contains("-04-01-")){
			returnValue = (float) 0;
		} else if (d = null){
			returnValue = (float) 0.5;
		} else{
			returnValue = (float) 0.5;
		}
		
                log("final date trust: " + returnValue);
		//return a value between 0 and 1 for credibility
		return returnValue;
	}
    
    /*
     * @author Ed S.
     * i use this function as a shorthand/force of habit, pls ignore me 
     */
    private void log(String s) {
        System.out.println(s);
    }
        


public static void getInfo() {
    BufferedReader reader;
    String line;
    StringBuffer responseContent = new StringBuffer();
    try {
        URL url = new URL("http://jsonplaceholder.typicode.com");
        connection = (HttpURLConnection) url.openConnection();
         
        //request setup
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
    
        int status = connection.getResponseCode();
            
        if (status > 299) {
            reader = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
             }
            reader.close();
        }
        else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
        }
        System.out.println(responseContent.toString());
    } 
    catch (MalformedURLException e) {
        e.printStackTrace();
    } 
    catch (IOException e) {
        e.printStackTrace();
    }
    finally {
        connection.disconnect();
    }
}

}