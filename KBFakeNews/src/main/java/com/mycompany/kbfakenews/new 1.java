import java.util.*

public class checkDate {
	public static void main(String[] args){
		
		//set variables
		int date;
		int currentYear = Year.now().getValue();
		float returnValue=0;

		//import variables from json
		
		
		//if statement to check date against ranges and return float value
		if (date<=currentYear && date>(currentYear-10)){
			returnValue = 1;
		} else if (date<=(currentYear-10) && date>(currentYear-20)){
			returnValue = .75;
		} else if (date>currentYear) {
			returnValue = 0;
		} else {
			returnValue = .5;
		}
		
		//return
		return returnValue;
	}
}