package wikiSearch.main;


import java.io.IOException;

import wikiSearch.merger.MergeIndex;
import wikiSearch.parsing.SAXParsing;
import wikiSearch.second.SecondaryIndex;



public class Main {
	public static void main(String args[])
	{
		long fileCount = 0;
		String source = "/home/kanika/Downloads/enwiki-latest-pages-articles.xml";
		String myFolder = "/home/kanika/index_kanika5";
		SAXParsing parse = new SAXParsing();
		
		double start = System.currentTimeMillis();
		double end;
		double total;
		fileCount = parse.parseMethod(source,myFolder);
		
		if(fileCount > 0){
			System.out.println("Merging Starts...........");
			MergeIndex mg = new MergeIndex();
			try {
				mg.mergeFunction(fileCount, myFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Secondary Index starts.......");
		SecondaryIndex si = new SecondaryIndex();
		try {
			si.CreateSecondary(myFolder,1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("back");
		try {
			si.CreateSecondary(myFolder,2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end=System.currentTimeMillis();
		total=end-start;
		System.out.println("Total running time: "+total/1000+" sec");
	}
}