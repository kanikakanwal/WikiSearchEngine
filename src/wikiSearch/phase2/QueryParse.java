package wikiSearch.phase2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import wikiSearch.Helpers.Stemmer;
import wikiSearch.Helpers.StoprWords;
import wikiSearch.second.SearchClass;
import wikiSearch.second.SecondIndexClass;


public class QueryParse {
	public List<SecondIndexClass> secondIndex,secondIdMapIndex;
	public Map<String, Double> tfidscoremap;
	public int flagfield;
	private String myFolder;
	public RandomAccessFile rafilemap;
	private StoprWords stopwords;
	private Stemmer stem;

	public QueryParse(String myFolder) throws Exception
	{
		flagfield = -1;
		this.myFolder = myFolder;
		secondIndex = new ArrayList<SecondIndexClass>();
		secondIdMapIndex = new ArrayList<SecondIndexClass>();
		SecondaryIndexload(myFolder);
		stopwords = new StoprWords();
		stem = new Stemmer();
		rafilemap = new RandomAccessFile(myFolder+"/mapIdTitle", "r");
		
	}
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		rafilemap.close();
	}
	public void QuerySearch(String query) throws Exception {
		// TODO Auto-generated method stub
		SearchClass st;
		tfidscoremap = new ConcurrentHashMap<String, Double>();
		ArrayList<SearchClass> stlist = new ArrayList<SearchClass>();
		StringTokenizer st1 = new StringTokenizer(query);
		while (st1.hasMoreElements()) {
			String str = st1.nextToken();
			int index = str.indexOf(':');
			flagfield = 0;
//			System.out.println("for:"+str+"<<");
			if(index != -1)
			{
				if(str.charAt(0)=='t'){
//					System.out.print("Title Search:");
					flagfield = 1;
				}
				else if(str.charAt(0)=='i'){
//					System.out.print("Infobox Search:");
					flagfield = 2;
				}
				else if(str.charAt(0)=='b'){
//					System.out.print("Text Search:");
					flagfield = 3;
				}
				else if(str.charAt(0)=='c'){
//					System.out.print("Category Search:");
					flagfield = 4;
				}
				else if(str.charAt(0)=='l'){
//					System.out.print("Link Search:");
					flagfield = 5;
				}
				
				else if(str.charAt(0)=='r'){
//					System.out.print("References Search:");
					flagfield = 6;
				}
				str = str.substring(index+1);
				System.out.println(str);
			}
			str = str.toLowerCase();
			if(!stopwords.isStopWords(str)&&flagfield!=1){
//				System.out.println("yes");
				continue;
			}
			stem.add(str.toCharArray(), str.length());
			stem.stem();
			str = stem.toString();
			if(!stopwords.isStopWords(str)&&flagfield!=1){
				continue;
			}
			st = new SearchClass(str, myFolder, flagfield,secondIndex, tfidscoremap);
			st.start();
			stlist.add(st);
		}

		for(SearchClass st11 : stlist){
			st11.join();
		}
		long resultSize = tfidscoremap.size();
//		System.out.println("size:"+tfidscoremap.size());
//		System.out.println("tf id map:"+tfidscoremap);
		if(resultSize == 0){
			System.out.println("No Matching Result!");
		}

		for(int i = 0;(i<10 && i<resultSize);i++){
			Double max = 0.0;
			String docid = "";
			Set<Entry<String, Double>> set = tfidscoremap.entrySet();

			Map.Entry<String, Double> e;
			Iterator<Entry<String, Double>> it = set.iterator();
			while (it.hasNext()){
				e=it.next();
				if(max < e.getValue()){
					max = e.getValue();
					docid = e.getKey();
				}	
			}


			try {
				searchRange2(docid);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			tfidscoremap.remove(docid);
		}

		tfidscoremap.clear();
	}

	private void searchRange2(String entry) throws Exception {
		// TODO Auto-generated method stub

		ArrayList<Long> index=new ArrayList<Long>(2);
		index.add(0, (long) -1); 
		index.add(1, (long) -1);
		binarySearchMap(entry, index);
		byte[] range;
		//		System.out.println("aftr:"+index.get(0)+" "+index.get(1));
		if(index.get(1) != -1)
			range = new byte[(int)(index.get(1)-index.get(0)+1)];
		else 
		{
			System.out.println("last:"+index.get(0));
			range = new byte[(int)(499703044-index.get(0))];
		}
		rafilemap.seek(Integer.parseInt(index.get(0).toString()));
		if(index.get(1) != -1)
			rafilemap.read(range, 0, (int)(index.get(1)-index.get(0)));
		else
			rafilemap.read(range);
		String value = new String(range);

		printToConsole(entry,value);


	}


	private void printToConsole(String entry, String value) throws Exception{
		// TODO Auto-generated method stub
		String wordFound;
		int len = value.length();
		for(int i=0;i<len;)
		{
			if(value.charAt(i) == '\n')
				break;

			int ind = value.indexOf(':', i);
			wordFound = value.substring(i, ind);
			i = ind+1;
			if(!entry.matches(wordFound.toString()))
			{
				while(value.charAt(i) != '\n')
				{
					i++;
					if(i == len)
					{
						return;
					}
				}
				i++;
			}
			else{
				System.out.print(entry +"  >>  ");
				ind = value.indexOf('\n', i);
				String output = value.substring(i, ind);
				i = ind;
				System.out.println(output);
				return;
			}			

		}

	}


	private void binarySearchMap(String entry, ArrayList<Long> index) throws Exception {
		// TODO Auto-generated method stub

		int start, end;
		start = 0;
		end = secondIdMapIndex.size()-1;
		int mid;
		mid = (start+end)/2;
		while(start < (end-1)){

			if(Integer.parseInt(entry) == Integer.parseInt(secondIdMapIndex.get(mid).getKey())){
				
				index.add(0,secondIdMapIndex.get(mid).getOffset());
				index.add(1,secondIdMapIndex.get(mid+1).getOffset());
				return;
				
				
			}
			else
				if(Integer.parseInt(entry) > Integer.parseInt(secondIdMapIndex.get(mid).getKey())){
					start = mid;
				}
				else{
					end = mid;
				}
			mid = (start+end)/2;
		}

		if(Integer.parseInt(entry) < Integer.parseInt(secondIdMapIndex.get(secondIdMapIndex.size()-1).getKey())){
			index.add(0,secondIdMapIndex.get(start).getOffset());			
			index.add(1,secondIdMapIndex.get(start+1).getOffset());
		}
		else{
			index.add(0,secondIdMapIndex.get(start+1).getOffset());			
			index.add(1,(long)-1);
		}

		return;		
	}

	private void SecondaryIndexload(String myFolder) throws Exception{
		FileInputStream fs;
		DataInputStream in;
		BufferedReader br;

		try{
			fs = new FileInputStream(myFolder+"/secondaryIndex");
			in = new DataInputStream(fs);
			br = new BufferedReader(new InputStreamReader(in));

			String str;
			while((str = br.readLine())!=null){
				String key;
				long offset;
				int index;
				index = str.indexOf(':');
				key = str.substring(0, index);
				offset = Long.parseLong(str.substring(index+1));
				SecondIndexClass temp = new SecondIndexClass(key, offset);
				secondIndex.add(temp);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}

		try{
			fs = new FileInputStream(myFolder+"/secondIdTitleIndex");
			in = new DataInputStream(fs);
			br = new BufferedReader(new InputStreamReader(in));

			String str;
			while((str = br.readLine())!=null){
				String key;
				long offset;
				int index;
				index = str.indexOf(':');
				key = str.substring(0, index);
				offset = Long.parseLong(str.substring(index+1));
				SecondIndexClass temp = new SecondIndexClass(key, offset);
				secondIdMapIndex.add(temp);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
