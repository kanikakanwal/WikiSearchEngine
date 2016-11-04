package wikiSearch.second;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wikiSearch.Helpers.CountFrequency;



public class SearchClass extends Thread {
	private int flagfield;
	private List<SecondIndexClass> secondindex;
	private Map<String, Double> tfidmap;
	private double N;
	private RandomAccessFile raf;
	private String word;


	public SearchClass(String word,String myFolder, int flagfield, List<SecondIndexClass> secondindex, Map<String, Double> tfidmap) throws FileNotFoundException
	{
		this.tfidmap = tfidmap;
		this.flagfield = flagfield;		
		this.word = word;
		N = Math.log(16299475);
		this.secondindex = secondindex;
		raf = new RandomAccessFile(myFolder+"/finalindex", "r");

	}

	@Override
	public void run(){
		try{
			if(word.length() != 0)
//				System.out.println("find "+word);
				searchRange1(word);
		}
		catch (Exception e ){
			return;
		}
	}

	public void searchRange1(String word) throws Exception {
		// TODO Auto-generated method stub		
		ArrayList<Long> index=new ArrayList<Long>(2);
		index.add(0, (long) -1); 
		index.add(1, (long) -1);
		BinarySearch1(word, index);
		byte[] result;
		
		if(index.get(1) != -1)
			result = new byte[(int)((index.get(1)-index.get(0))+1)];
		else 
			result = new byte[(int) (1068)];
		try {
			raf.seek(index.get(0));
			if(index.get(1) != -1)
				raf.read(result, 0, (int)(index.get(1)-index.get(0)));
			else{
				
				raf.read(result);
//				System.out.println("result:"+result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans = new String(result);
		try{
//			System.out.println("ans:"+ans);
			calculateRank(word, ans);
		}
		catch(Exception e)
		{
			return;
		}	
	}	
	private void calculateRank(String searchstr, String result) throws Exception {
		int flagfield = 0;
		int index;
		int i;
		String[] resultlist=result.split("\n");
//		System.out.println("res.len"+result.length());
		for(String resultelem : resultlist)
		{
//			System.out.println("fo "+resultelem);
			index = resultelem.indexOf(':');
			String word = resultelem.substring(0, index);
			if(word.compareToIgnoreCase(searchstr) > 0)
			{
//				System.out.println("out "+word.compareToIgnoreCase(str));
				break;
				}
			else{
//				System.out.println("else "+word.compareToIgnoreCase(str));
//				System.out.println("compare:"+word+" "+str);
				i = index+1;
				if(word.matches(searchstr)){
//					System.out.println("match:"+word);
					int prev = 0;
					String docid = "";
					int totalDoc = 0;
//					System.out.println("resultelem:"+resultelem);
//					System.out.println("length:"+resultelem.length());
					index = resultelem.indexOf(':', i);
					totalDoc = Integer.parseInt(resultelem.substring(i, index));
//					System.out.println("totaldoc:"+totalDoc);
					i = index+1;
					int pc = 0;
					for(int k = i; k<resultelem.length() && pc <= totalDoc;k++,pc++){
						index = resultelem.indexOf('*', k);
						docid = String.valueOf(Integer.parseInt(resultelem.substring(k, index))+prev);
						prev = Integer.parseInt(docid);
						CountFrequency cf = new CountFrequency(docid);
						k = index+1;
						while(resultelem.charAt(k) != '|')
						{
							if(k+1 == resultelem.length())
							{
								break;
							}
							if(resultelem.charAt(k) == '*') 
							{
								k++;
								continue;
							}
							switch(resultelem.charAt(k))
							{

							case 't' : flagfield = 1;
							break;
							case 'i' : flagfield = 2;
							break;
							case 'b' : flagfield = 3;
							break;
							case 'r' : flagfield = 6;
							break;
							case 'L' : flagfield = 5;
							break;
							case 'c' : flagfield = 4;
							break;
							case 'l' : flagfield = 7;
							break;
							default : flagfield = 0;
							}
							if(flagfield != 0)
							{
								index = Math.min(resultelem.indexOf('*', k), resultelem.indexOf('|', k));
								if(index == -1)
								{
									index = resultelem.indexOf('|', k);
								}

								int count = Integer.parseInt(resultelem.substring(k+1, index));
								//System.out.print(count +" ");
								cf.incrementCounterByValue(flagfield, count);
								k = index;

							}
						}

						storeRank(searchstr, docid, cf, totalDoc);
					}
					break;
				}
			
			}
				
		}

	}

	private void storeRank(String str, String docid, CountFrequency fc, int totalDoc) throws Exception {
		// TODO Auto-generated method stub
		Double count = 0.0;
		switch(flagfield)
		{
		case 1 : {
			count = count + (fc.wikiTitle());
			//	System.out.println("case 1 "+fc.wikiTitle()+"<");
			break;
		}
		case 2 : {
			count = count +  (fc.wikiInfo());
			break;
		}
		case 3 : {
			count = count + fc.wikiBody();
			break;
		}
		case 4 : {
			count = count + fc.wikiCategory();
			break;
		}
		case 5 : {
			count = count + (fc.wikiLink()+fc.wikiinlinks());
			break;
		}
		case 6 : {	
			count = count + fc.wikiReferences();
			break;
		}
		case 0 : {

			count = count + fc.getScore();	
			//						System.out.println("case 0 "+fc.getScore()+"<");
			break;
		}
		}
		if(count == 0.0)
			return;
		Double value = (Double)count*(N - Math.log(totalDoc));
		if(!tfidmap.containsKey(docid)){
//			System.out.println("doc"+docid+"val if:"+ value);
			tfidmap.put(docid, value);
		}
		else{
//			System.out.println("val else:"+tfidmap.get(docid) * value);
			tfidmap.put(docid, tfidmap.get(docid) * value);
		}
		return;
	}

	private void BinarySearch1(String str, ArrayList<Long>index)  throws Exception {
		// TODO Auto-generated method stub

		int start, end;
		start = 0;
		end = secondindex.size()-1;
		int mid = (start+end)/2;
		while(start < (end-1)){
			if(str.compareTo(secondindex.get(mid).getKey()) == 0){
				index.add(0,secondindex.get(mid).getOffset());
				index.add(1,secondindex.get(mid+1).getOffset());
				return;
				
			}
			else if(str.compareTo(secondindex.get(mid).getKey()) > 0){
				start = mid;
			}
			else{
				end = mid;
			}
			mid = (start+end)/2;
		}

		if(str.compareTo(secondindex.get(secondindex.size()-1).getKey()) < 0){
//			System.out.println("word in if:"+secondindex.get(start).getOffset());
			index.add(0,secondindex.get(start).getOffset());			
			index.add(1,secondindex.get(start+1).getOffset());
		}
		else{
//			System.out.println("word in else:"+secondindex.get(start+1).getOffset());
			index.add(0,secondindex.get(start+1).getOffset());			
			index.add(1,(long)-1);
		}

		return;		
	}

}
