package com.mycompany.kbfakenews;
import java.util.*;
// JSON imports
import java.io.IOException;
import org.json.simple.JSONObject;
// JSoup imports
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 *
 * @author Rashad, Ed S., Ammar
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
        authorJ = "charles darwin";
        URLJ = "";
        dateJ = "";
        contentJ = "";
    }
    
    //calling avarage test results
    //articleJ is the json file
    public static void main(String[] args) throws IOException{
        System.out.println("running");
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
    //returns a value of 100 if the website is credible
    //returns 0 if website is not credible


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
    		// store name
    		String name = link.text();
    		// store link
    		String address = link.attr("abs:href");
    		// debug: log(name);
    		// debug: log(address);
    		if(name.toLowerCase().contains(first) && name.toLowerCase().contains(last)) {
    			log("match found, parsing " + name + "'s profile.");
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
    	System.out.println("citations: " + citations);		// citations
    	System.out.println("r_citations: " + r_citations);	// citations since 2015
    	System.out.println("hindex: " + hindex);		// h-index
    	System.out.println("r_hindex: " + r_hindex);		// h-index since 2015
    	System.out.println("i10index: " + i10index);		// i10-index
    	System.out.println("r_i10index: " + r_i10index);	// i10-index since 2015

    	// super top secret complex algorithm at work: 
    	float hvalue = (float) (Math.min((r_hindex/25.0), 1.0) * 0.3);			// hindex scales between 0-25, worth 3/10 of total
    	float cvalue = (float) (Math.min(r_citations/1500.0, 1.0) * 0.2);		// citation scales between 0-1500, worth 1/5 of total
    	float i10value = (float) (Math.min(r_i10index/50.0, 1.0) * 0.2);		// i10index scales between 0-50, worth  of total
    	
    	hvalue += (float) (Math.min(((r_hindex-25.0)/25.0), 1.0) * 0.1);		// hindex scales further between 25-50, worth 1/10 of total
    	cvalue += (float) (Math.min((r_citations-1500.0)/3500.0, 1.0) * 0.1);           // citation scales further between 1500-5000, worth 1/10 of total
    	i10value += (float) (Math.min((r_i10index-50.0)/50.0, 1.0) * 0.1);		// i10index scales further between 50-100, worth 1/10 of total
        
        trust = hvalue + cvalue + i10value;
    	System.out.println("final author trust: " + trust);	// i10-index since 2015
    	
    	return trust;
    }
    //returns a value of 0-100 if the preson has has a i-10 index higheer 
    //then a determined ammount that exceeds the avarage

    
    //article check 
    private float articleCheck(String content){
	
	return 0;
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
			returnValue = 100;
		} else if ((year <= (currentYear-10)) && (year > (currentYear-20))){
			returnValue=75;
		} else if (year > currentYear){
			returnValue = 0;
		} else{
			returnValue = 50;
		}
		
		//return a value between 0 and 100 for credibility
		return returnValue;
	}
    
    /*
     * @author Ed S.
     * i use this function as a shorthand/force of habit, pls ignore me 
     */
    private void log(String s) {
        System.out.println(s);
    }

}