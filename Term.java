package ca.queensu.cics124;


import java.util.ArrayList;
import java.util.HashMap;

//Class to create Term objects representing each phenotype 
public class Term {

	//Constants to hold the key values of the attributes of term objects according to how they are
	//named in the HPO.txt file
	public static final String SECTION_TERM = "[Term]";
	public static final String KEY_ID = "id";
	public static final String KEY_ALT_ID = "alt_id";
	public static final String KEY_IS_A = "is_a";
	public static final String KEY_IS_OBSOLETE = "is_obsolete";
	
	public Term() {
	}
	
	//ID attribute
	private String id;

	//Array list to hold all the parent nodes of the term
	private ArrayList<String> is_aList;

	//Obsolete attribute
	private boolean obsolete;
	
	// there is only one instance of root Term
	private static Term root; 
	
	// holds all of the term's text information, which is used to answer the queries 
	private String termStringData="";  
	
	//one parent per term 
	private Term parent;  
	
	
	//Attribute: Array list of all the child term objects of the term 
	private ArrayList<Term> childTermList;
	

	// Map to hold all the term IDs and their term objects --> key: ID, value: Term object
	public static HashMap< String, Term> IdTermsMap = new HashMap< String, Term>();
	
	// Map to hold the Parent IDs and their list of child IDs --> key: parent ID, value: List of child IDs
	private static HashMap< String, ArrayList<String>> parentChildMap = new HashMap< String, ArrayList<String>>();

	
	//returns whether the term is obsolete
	public boolean isObsolete() {
		return this.obsolete;
	}
	
	//sets the obsolete value to the input
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}
	
	//Determines if a term is the root 
	//Input: String of term's ID
	//Output: True if id is the root, false otherwise
	public static boolean isRoot( String id ) 
	{
		
		if (root!=null) 
		{
			//if root's ID equals to the id of the input, return true
			return (root.getId().equalsIgnoreCase(id));
		}
		return false;
	}
	
	//getters and setters for termStringData and ID attributes 
	public String getTermStringData() {
		return termStringData;
	}
	
	public void setTermStringData(String termBuffer) {
		this.termStringData = termBuffer;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	//returns the list of parent nodes of a term 
	public ArrayList<String> getIs_aList() {
		return is_aList;
	}
	
	//sets the is_a list to the is_a list inputted 
	public void setIs_aList(ArrayList<String> is_aList) {
		this.is_aList = is_aList;
	}

	
	//Adds a new parent ID to the list of parent IDs of the term object 
	public void addIs_a(String is_a )
	{
		//creates new parent list 
		if (is_aList == null) 
		{
			is_aList = new ArrayList<String>();		
		}
		
		//adds the parent ID 
		is_aList.add(is_a);
		
	}
	
	//getter and setter for the parent of the term 
	public Term getParent() {
		return parent;
	}
	
	public void setParent(Term parent) {
		this.parent = parent;
	}
	
	//getters and setters for the list of child terms of the term object
	public ArrayList<Term> getChildTermList() {
		return childTermList;
	}
	public void setChildTermList(ArrayList<Term> childList) {
		this.childTermList = childList;
	}
	
	
	//Adds a child term to the childTermList or creates the childTermList if one does not currently exist
	private void addChild( Term child ) {
		if ( childTermList == null)
		{
			childTermList = new ArrayList<Term>();
		}
		childTermList.add(child);
	}
	
	

	// Populate term object's attributes based on line read
	// Only id, is_a and obsolete lines are relevant to be parsed
	// Input: Term object and String object of the current line being read  
	private static void parseKey(Term term, String line) {
		
		//line is not null and is an empty string , exit 
		if (line != null && line.isEmpty()) {
			return;
		}
		
		//Splits the line into indexes in an array of a key and a value (0th index is the key,
		//1st and/or 2nd index is the value
		String [] keyValue = line.split(":");
		
		
		String key ="";
		String value = "";
		
		// save the Term data, adding on to the lines previously saved 
		term.setTermStringData(term.getTermStringData() + ( line + "\r\n")) ;
		
		//if the keyValue array's length is greater than 0 and the first index is not empty, save the key
		if (keyValue.length>0  && !keyValue[0].isEmpty())
			key =  keyValue[0].trim();
		
		//if the key value pair's length is greater than 0 and the second index is not empty, save the value
		if (keyValue.length>1 && !keyValue[1].isEmpty())
			value =  keyValue[1].trim();
	
		if (key !=null && value !=null) {
			
			switch (key) {
				
				//if the key is a KEY_ID
				case Term.KEY_ID:
					
					//checks that the keyValue length is greater than 2
					if (keyValue.length>2) {
						
						//set the id of the term object (2nd index because the line has extra ":")
						term.setId("HP:"+ keyValue[2]);  		
					}
					break;	
					
				//if the key is a IS_A 
				case Term.KEY_IS_A:
					
					if (keyValue.length>2) {
						
						//Set new string variable to the keyValue's 2nd index (index of the ID of the parent node)
						//current example value of s = "0000277 ! Abnormality of the mandible"
						String s = keyValue[2];
						
						//Get only the ID of the parent by splitting the string 
						//current example value of s1 = "[0000277, !, Abnormality, of, the, mandible]"
						String s1[] = s.split(" ");
						
						// adds the ID of the parent term of the current term object to the 
						// object's list of parent terms --> is_aList 
						term.addIs_a("HP:"+s1[0].trim());  					
					}
					break;
				
				//if the key is a IS_OBSOLETE 
				case Term.KEY_IS_OBSOLETE:
					
					//set obsolete value 
					if (value.equalsIgnoreCase("true")) {
						term.setObsolete(true);
					} else {
						term.setObsolete(false);
					}
					break;
			}
		}
	}

	//creates new term objects and builds the parent-child dictionary of the terms 
	//Input: array list of the term data of a single term object read from the file  
	public static void createTerm(ArrayList<String> lines)
	{

		//if the lines array is not empty 
		if (lines.isEmpty() == false) 
		{
			
			//create new term object
			Term term = new Term();
			
			//for each line in the lines array,	populate the term object's attributes based on lines read
			//in the "lines" array
			for( String line : lines) 
			{
				parseKey( term, line);
			}		
			
			//if the term has an ID value, add it to the IdTermsMap mapping IDs to the term objects 
		    if (term.getId()!=null) {
		    	IdTermsMap.put(term.getId(), term);
		    }
		    
		    // setup parent child hash map so that the child of any node can be 
		    // looked up quickly later in tree build
		    // key: parent ID
		    // value: list of child IDs
		    if (term.getId()!=null && term.getIs_aList() != null) { 
		    	
		    	//for EACH PARENT ID in the "is_aList" array, 
		    	for ( String parent_id : term.getIs_aList()) 
		    	{		
		    		
		    		//Gets the list of strings of children IDs of the current parent ID and places them into
		    		//the childList array 
		    		ArrayList<String> childIdList = parentChildMap.get(parent_id);
		    		
		    		//if childIdList does not currently exist, create one and put the current parent_Id as the 
		    		//key, current child list as the value 
		    		if (childIdList == null) {
		    			
		    			childIdList = new ArrayList<String>();
			    		
		    			parentChildMap.put(parent_id, childIdList);
		    		}
		    		
		    		//otherwise, add the child's ID to the list 
		    		childIdList.add(term.getId());
		    	}
		    }			
		}
	}
	
	
	//creates child terms from a parent term 
	private static void buildChild( Term parent )
	{
		
		//gets the child IDs of the parent term inputted and places them into the childIdList 
		ArrayList<String> childIdList = parentChildMap.get(parent.getId());
		
		
		if (childIdList != null) 
		{
			//for each child ID in the childIdList, 
			for( String child_id : childIdList) {
				
				//gets the term object of the child ID specified and assigns it to a new term object 
				Term childTerm = IdTermsMap.get(child_id);

				//sets the child term's parent 
				childTerm.setParent(parent);
				
				//adds the child to the parent term object's childTermList
				parent.addChild(childTerm);
				
				// recursively finds the current child's children
				buildChild( childTerm ); 
			}
		}
		
	}
	
	//
	//Output: Root term object
	private static Term findRootTerm() 
	{
		//for each term in the values of the IdTermsMap (which are term objects),
		for ( Term term: IdTermsMap.values()) {
			
			//if the term is not obsolete and the term does not have any Is_a relationships (no parents)
			//it is the root term
			if ( !term.isObsolete() && (term.getIs_aList() == null)) {  
				return term;
			}
		}
		
		return null;
	}
	
	//builds the parent-child tree by first finding the root term
	//then, recursively links the term objects together using the hashmap of the IDs of the parents and their
	//children, which was created when the file was read
	//Output: Returns the root term 
	public static Term buildHPOTree() {
		root = findRootTerm();			
		buildChild ( root );	
		return root;
		
	}
	
	
	//returns an array list of term objects of the term's parents up to the root node 
	//Input: String of the term object's ID that a path is required for 
	public static ArrayList<Term> getTermPathById( String id) {
		
		//path of terms starting from the current term to the root term
		ArrayList<Term> termsPath = new ArrayList<Term>();
		
		
		Term term = IdTermsMap.get(id);
		
		
		if (term !=null) 
		{
			// include the current term in the termPath
			termsPath.add(term); 
			
			//while the term still has a parent term, keep adding terms to the termsPath 
			//once the parent of the current term is null, the root term has been reached
			while ( null != (term = term.getParent())) {  
					termsPath.add(term);
			}
			
		} 
		
		//Display error message if a query with an incorrect ID is in the query.txt file  
		else 
		{
			System.err.println("Term: " + id + " not found!");
		}
		
		
		return termsPath;		
		
	}
	
	
}
