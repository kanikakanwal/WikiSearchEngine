package wikiSearch.Helpers;

public class CountFrequency {

	
	private String docId;
	private int ctitle, cinfo, cbody, ccategory, clink, creferences, cinlinks, total;
	
	public CountFrequency(String id)
	{
		docId = id;
		ctitle = cinfo = cbody = ccategory = clink = creferences = cinlinks = total = 0;
	}
	public String wikiId(){
		return docId;
	}
	
	public void incrementCounter(int flag){
		total++;
		switch(flag){
		
			case 1 : ctitle++;
					 break;
			case 2 : cinfo++;
					 break;
			case 3 : cbody++;
			 		 break;
			case 4 : ccategory++;
					 break;
			case 5 : clink++;
			 		 break;
			case 6 : creferences++;
			 		 break;
			case 7 : cinlinks++;
	 		 		 break;
		}		
	}
	public void incrementCounterByValue(int flag, int value){
		
		total += value;
		switch(flag){
		
			case 1 : ctitle += value;
					 break;
			case 2 : cinfo += value;
					 break;
			case 3 : //cbody += value;
					cbody++;
			 		 break;
			case 4 : ccategory += value;
					 break;
			case 5 : clink += value;
			 		 break;
			case 6 : creferences += value;
			 		 break;
			case 7 : cinlinks += value;
	 		 		 break;
		}		
	}
	
	public Double getScore(){
		return (ctitle*18.0 + cinfo*5.0 + total) ;
		
	}
	
	public int wikiTitle(){
		return ctitle;
	}
	public int wikiInfo(){
		return cinfo;
	}
	public int wikiBody(){
		return cbody;
	}
	public int wikiCategory(){
		return ccategory;
	}
	public int wikiLink(){
		return clink;
	}
	public int wikiReferences(){
		return creferences;
	}
	public int wikiinlinks(){
		return cinlinks;
	}
	public int wikitotal(){
		return total;
	}
	


}
