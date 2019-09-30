package ca.queensu.cics124;

import java.util.ArrayList;


//Class used to process queries from the queries.txt file 
public class Query {

	public Query() {
	}

	//Process a query by getting the path of the queried term to the root term 
	//Input: String "line" of the current query being read, String "section" to divide each query answer
	//Output: String answer of the path from the queried term to the root term 
	public static String processQuery(String line, String section) {

		//split line by " " 
		String s[]=line.split(" ");
		
		//string to keep track of the query answer
		String pathAnswer = section;
		
		
		if (s[0]!=null && s[0].equalsIgnoreCase("query:")) {
			
			// do not consider root node as it has no parents
			if (Term.isRoot(s[1])) return ""; 
			
			//get an array list of terms that represent the path from the current term to the root node 
			ArrayList<Term> termsPath = Term.getTermPathById(s[1]);
			
			
			if (termsPath != null) {
				
				//for each term in the termsPath, 
				for (Term term: termsPath) {
					
					//gets the term's information and adds it to the answer, displaying the path from the current
					//term to the root 
					pathAnswer = pathAnswer + term.getTermStringData();
					
					//each answer ends with a blank line
					pathAnswer = pathAnswer + "\r\n";
				}
			}
		}
		return pathAnswer;
	}


	
	
}
