package wikiSearch.phase2;

import java.util.Scanner;

public class QueryInput {
	public static void main(String args[]) 
	{
		String myFolder = args[0];
		
			QueryParse queryParse = null;
			try {
				queryParse = new QueryParse(myFolder);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String query;
			Scanner in ;
			do
			{
				in= new Scanner(System.in);
				System.out.println("Search: ");
				query = in.nextLine();
				double start = System.currentTimeMillis();
				try {
					queryParse.QuerySearch(query);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				double end = System.currentTimeMillis();
				double totalTime = end - start;
				System.out.println("SEARCH TIME : "+ totalTime/1000 + " sec");
				System.out.print("Want another Query (y for YES) : ");
			}while((in.nextLine()).equalsIgnoreCase("y"));
			in.close();
			System.out.println();
		

	}	


}
