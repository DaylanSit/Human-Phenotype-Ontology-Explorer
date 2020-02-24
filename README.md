# Human-Phenotype-Ontology-Explorer
Software tool that allows user to query the Human Phenotype Ontology, which contains a formal representation of the manifestation of diseases in the different human phenotypes organized as an acyclic graph 


# Files
- A class Term to keep track of each of the concepts (i.e., terms) defined in the ontology.
- A class Query to store input queries and their results.
- A main driver class HPOExplorer to: 
  1. Read the ontology’s text file, parse it and build the data structures that support exploring it
  2. Read a set of queries from a text file and produce the applicable results in another formatted text file
  3. Produce a text file displaying the results of a general query on the entire ontology
