/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kbfakenews;

/**
 *
 * @author Rashad
 **/
public class KBTest {
    //knowledge based fake news detector
    //calling avarage test results
    //articleJ is the json file
    public static void main(String[] args){
	 float result = resultAVG(articleJ);
   }
}

    //avg Check
    resultAVG(JSONObject o){
	//calculating avarage
	float avg = (webCheck(o) + authorCheck(o) + articleCheck(o) + dateCheck(o))/4)
	return avg;
}

    //website check
    webCheck(JSONObject a){
       float ans= ;
       return ans;
    }
    //returns a value of 100 if the website is credible
    //returns 0 if website is not credible


    //author check
    authorCheck(JSONObject b){
	float ans= ;
	return ans;
    }
    //returns a value of 0-100 if the preson has has a i-10 index higheer 
    //then a determined ammount that exceeds the avarage

    
    //article check 
    articleCheck(JSONObject c){
	float ans= ;
	return ans;
    }
    /*returns a value corrisponding to articles credibility.
    if the article has been cited before it adds either 0 to 100 to the output 
    depending on what criteria we have for the ratio of citations.*/

    //date check
    dateCheck(JSONObject d){
	float ans= ;
	return ans;
    }

