/* 
HPO Explorer Tool
Name: Daylan Sit
Student Number: 10179650
Date: February 25, 2019

This program is a software prototype tool used to explore a biomedical ontology of human disease 
description terms in response to a simple type of queries about these terms. 
HPO Explorer receives these queries from, and produces results to, formatted text files.
 */

package ca.queensu.cics124;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//HPOExplorer main class, creates term objects and processes queries
//Writes answers to the maxpath.txt and result.txt files 
public class HPOExplorer {
	
	public HPOExplorer() {
	}

	
	/*
	 *  Reads all the information of all the term nodes from the HPO.txt file
	 *  Creates term objects and their parent-child relationships using a dictionary
	 *  Input: String of the name of the file 
	 */
	public static void readHPOFile(String filename) {

		// Try block to catch IO/file not found error, creates BufferReader object
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			// creates string array list to store the lines of the file
			ArrayList<String> lines = new ArrayList<String>();

			// stores the current line that BufferedReader is reading
			String line;

			// Skip manifest info until the first [term]
			while ((line = br.readLine()) != null) {

				if (line.trim().equalsIgnoreCase(Term.SECTION_TERM)) {
					
					//reads the first instance of the string [Term]
					lines.add(line);
					
					break; // if reached end of file than stop
				}
			}
			
			//keeps track of whether end of file is reached 
			boolean endOfFile = false;

			
			//reads the terms and their attributes into the "lines" array until the end of the file
			//is reached 
			do {
				
				//reads next line 
				line = br.readLine();

				//If the current line is null, the reader is at the end of the file
				if (line == null) {
					
					//if lines array is not empty, must add the last term still 
					if (lines.isEmpty() == false) 
					{
						Term.createTerm(lines);
					}
					
					//reached the end of the file
					endOfFile = true;

					
				} 
				
				// creates the term objects throughout the file, once the reader has reached the start
				// of the next term 
				else if (line.trim().equalsIgnoreCase(Term.SECTION_TERM))
				{

					// if the lines array is not empty
					if (!lines.isEmpty()) 
					
					{ 
						//create a new term object using the lines that have been previously read 
						Term.createTerm(lines);
						
						// clear out string of lines for the next term
						lines.clear(); 
						
						// add the "[TERM]" line to the "lines" array (the current line being read)
						lines.add(line); 
					}
				} 
				
				//otherwise, add the current line to the "lines" array 
				else 
				{
					lines.add(line);
				}

			} while (!endOfFile);
			
			br.close();
			
		} catch (IOException e) {
			System.err.println("Error reading HPO file");
		}
	}
	
	
	//Read the query file and write the answers to the results.txt file 
	public static void processQueryFile(String filename, String resultFile) {

		// reads the queries.txt file
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String line;

			FileWriter fw = new FileWriter(resultFile, false);
			
			BufferedWriter bw = new BufferedWriter(fw);

			// while there are still lines to read
			while ((line = br.readLine()) != null) 
			{

				//Process the query and save it to the string "answer"
				String answer = Query.processQuery(line, "[query_answer]\r\n");

				//write the query answer to the file 
				if (answer != null) 
				{
					bw.write(answer);
				}
			}
			bw.close();
			br.close();
			
		} 
		catch (IOException e) 
		{
			System.err.println("Error reading query file");
		}
	}
	
	//gets all the term sections corresponding to the longest path from a node to the root node
	//and writes it to the maxpath.txt file 
	private static void getMaxPath(String maxPathFile) {

		
		FileWriter fw;

		
		try {
			
			//length of the longest path
			int maxPath = 0;
			
			//ID of the node term that has the longest path to the root 
			String maxId = "";
			
			
			//for each term of the terms of the IdTermsMap, 
			for( Term term : Term.IdTermsMap.values()) {
				
				//gets the term path of the term from the current term to the root and stores it in 
				//the termPathList array
				ArrayList<Term> termPathList = Term.getTermPathById(term.getId());
				
				//if the current length of the termPathList array is larger than the maxPath, 
				if (termPathList.size()>maxPath) 
				{
					//replace the maxPath with the current length of the termPathList
					maxPath = termPathList.size();
					
					//set the maxId to the ID of the current term 
					maxId = term.getId() ;
				}
			}
			
			//display length of the maximum path 
			String section = "[max_path=" + maxPath + "]\r\n";
			
			//query to get the path of the term with the maximum length 
			String query = "query: " + maxId;
			
			//gets the terms and their data of each term in the path from the current term to the root node
			String maxPathAnswer = Query.processQuery(query, section);
			
			
			if (maxPathAnswer != null) {
				
				fw = new FileWriter(maxPathFile, false);
				BufferedWriter bw = new BufferedWriter(fw);
				
				//write to the maxpath.txt file 
				bw.write( maxPathAnswer );
				bw.close();
		     }
			
		} catch (IOException e) {

			System.err.println("Error writing maxPathFile file");
		}
    	
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String hpoFile="HPO.txt";
		String queryFile="queries.txt";
		String resultFile="result.txt";
		String maxPathFile="maxpath.txt";
		
		
		//reads the HPO file, builds the term tree, processes and writes queries and max term path 
		readHPOFile(hpoFile);
		Term.buildHPOTree();		
		processQueryFile(queryFile, resultFile);
		getMaxPath( maxPathFile );

	}


}
