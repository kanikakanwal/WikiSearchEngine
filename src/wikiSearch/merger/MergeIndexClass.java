package wikiSearch.merger;

import java.util.ArrayList;

import wikiSearch.Helpers.CountFrequency;


public class MergeIndexClass {
	public ArrayList<CountFrequency> listfc;
	public ArrayList<Integer> file;
	public int counter;
	
	MergeIndexClass()
	{
		listfc = new ArrayList<CountFrequency>();
		file = new ArrayList<Integer>();
	}
	public void setCounter(String countc)
	{
		counter=counter+Integer.parseInt(countc);
	}

	public int getCounter()
	{
		return counter;
	}



}
