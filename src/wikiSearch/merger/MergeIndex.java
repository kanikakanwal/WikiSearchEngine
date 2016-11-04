package wikiSearch.merger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import wikiSearch.Helpers.CountFrequency;


public class MergeIndex {

	private Map<String, MergeIndexClass> finalmap;
	private List<CountFrequency> freqCount;
	private FileInputStream []fs;
	private DataInputStream []in;
	private BufferedReader []br;
	private HashMap<Integer, String> filemap;
	private String word, counter;
	private StringBuilder body;
	private BufferedWriter bw;
	
	
	
	
	public void mergeFunction(long fileCount,String myFolder) throws IOException
	{
		
		String fileName;
		filemap=new HashMap<Integer, String>();
		fs=new FileInputStream[(int) fileCount];
		in=new DataInputStream[(int) fileCount];
		
		File file = new File(myFolder + "/finalindex");
		file.createNewFile();
		br=new BufferedReader[(int) fileCount];
		bw = new BufferedWriter(new FileWriter(myFolder+"/finalindex"));
		finalmap = new TreeMap<String, MergeIndexClass>();
		word=new String();
		body=new StringBuilder();
		freqCount = new ArrayList<CountFrequency>();
		int n=0;
//		System.out.println("fileCount"+fileCount);
		for(int i=0;i<fileCount;i++,n++)
		{
			fileName = myFolder + "/file_"+(n+1);
			filemap.put(i, fileName);
			fs[i] = new FileInputStream(fileName);
			in[i] = new DataInputStream(fs[i]);
			br[i] = new BufferedReader(new InputStreamReader(in[i]));
			
			String read;
			if((read = br[i].readLine())!=null)
			{
				word = "";
				
//				System.out.println("caling traver with read"+read);
				traverseLine(read);
				if(!finalmap.containsKey(word)){
					
					
//					System.out.println("i m here##############");
					//finalmap.put(word.toString(), new MergeClass());
					MergeIndexClass mic = new MergeIndexClass();
					mic.file.add(i);
					mic.setCounter(counter);
					for(int k = 0;k<freqCount.size();k++){
						
						mic.listfc.add(freqCount.get(k));						
					}	
					finalmap.put(word,mic);
//					System.out.println(word);
					//if(word.equalsIgnoreCase("anupriya"))
					//	System.out.println(finalmap.get(word)+"heyyyy");

					
				}
				else if(finalmap.containsKey(word))
				{
					
//					System.out.println("contains:"+word);
					finalmap.get(word).file.add(i);
					finalmap.get(word).setCounter(counter);
					for(int k = 0;k<freqCount.size();k++){
//						System.out.println("fcccccccccccccccccccc:"+freqCount);
						finalmap.get(word).listfc.add(freqCount.get(k));
					}
							
				}
				
				freqCount.clear();
			}
					
		  }
		
		while(finalmap.size() != 0){
			ArrayList<Integer>filefc = null;
				filefc = printTopValue();
			for(Integer i : filefc){
				String read;
				if((read = br[i].readLine())!=null)
				{
					word = "";
					traverseLine(read);
					if(finalmap.containsKey(word)){
						finalmap.get(word).file.add(i);
						finalmap.get(word).setCounter(counter);
						for(int k = 0;k<freqCount.size();k++){
							finalmap.get(word).listfc.add(freqCount.get(k));
							
						}
					}
					else
					{
						//finalmap.put(word.toString(), new MergeClass());
						MergeIndexClass mc = new MergeIndexClass();
						mc.file.add(i);
						mc.setCounter(counter);
						for(int k = 0;k<freqCount.size();k++){
							
							mc.listfc.add(freqCount.get(k));						
						}	
						finalmap.put(word,mc);
						
					}
				}
				else
				{
					br[i].close();
//					System.out.println("Delete file "+filemap.get(i));
					//Files.deleteIfExists(Paths.get(filemap.get(i)));
				}
				freqCount.clear();
			   }
			
		}
			
	
				 if(body.length() != 0)
					 bw.write(body.toString());
				 bw.close();
			
//		System.out.println("Index File Created of line : "+lineCount);
	
	}
	
	private ArrayList<Integer> printTopValue() throws IOException {
		// TODO Auto-generated method stub
		 Iterator<Entry<String, MergeIndexClass>> it = finalmap.entrySet().iterator();
		 Entry<String, MergeIndexClass> me = it.next();
         body.append(me.getKey() + ":");
         body.append(me.getValue().getCounter() + ":");
         int pid = 0;
         MergeIndexClass mc = me.getValue();
         ArrayList<CountFrequency> freqCount = mc.listfc;
         ArrayList<Integer> filefc = mc.file;
         for(CountFrequency freq : freqCount){
        	 body.append(Integer.parseInt(freq.wikiId())-pid);
 			pid=Integer.parseInt(freq.wikiId());
 			if(freq.wikiTitle()!=0){
 				body.append("*t"+freq.wikiTitle());
 				
 			}
 			if(freq.wikiInfo()!=0){
 				body.append("*i"+freq.wikiInfo());
 				
 			}
 			if(freq.wikiBody()!=0){
 				body.append("*b"+freq.wikiBody());
 				
 			}
 			
 			if(freq.wikiReferences()!=0){
 				body.append("*r"+freq.wikiInfo());
 				
 			}
 			
 			if(freq.wikiLink()!=0){
 				body.append("*L"+freq.wikiLink());
 				
 			}
 			
 			if(freq.wikiCategory()!=0){
 				body.append("*c"+freq.wikiCategory());
 				
 			}
 			
 			if(freq.wikiinlinks()!=0){
 				body.append("*c"+freq.wikiinlinks());
 				
 			}
 			
        	 body.append("|");
        	 if(body.length() > 500){
            	 bw.write(body.toString());
            	 body = new StringBuilder("");
            	 
             }
        	       	
         }
         body.append("\n");
         //System.out.println();
         if(body.length() > 500){
        	 bw.write(body.toString());
        	 body = new StringBuilder("");
        	 
         }	
         finalmap.remove(me.getKey());
         return filefc;
	}

	private void traverseLine(String read) {
		// TODO Auto-generated method stub
		CountFrequency f ;
		int prev = 0;
		String docid = "";
		int flag = 0,flag1 = 0;
		for(int k = 0;k<read.length();k++)
		{
			if(read.charAt(k) == ':'){
				flag = 1;k++;
			}
			if(flag == 1){
				int indexid;
				if(flag1 == 0){
					counter = "";
					indexid = read.indexOf(':', k);
					counter = read.substring(k, indexid);
					k = indexid+1;
					flag1 = 1;
				}
				indexid = read.indexOf('*', k);
				docid = String.valueOf(Integer.parseInt(read.substring(k, indexid))+prev);
				prev = Integer.parseInt(docid);
				
				f = new CountFrequency(docid);
				k = indexid+1;
				while(read.charAt(k) != '|'){
					if(k+1 == read.length()){
						return;
					}
					if(read.charAt(k) == '*') {k++;continue;}
					int flagfeild = 0;
					
					switch(read.charAt(k)){
					
						case 't' : flagfeild = 1;
									 break;
						case 'i' : flagfeild = 2;
									break;
						case 'b' : flagfeild = 3;
				 		 			break;
						case 'r' : flagfeild = 6;
									break;
						case 'L' : flagfeild = 5;
									break;
						case 'c' : flagfeild = 4;
									break;
						case 'l' : flagfeild = 7;
									break;
						default : flagfeild = 0;
					}
					
					if(flagfeild != 0){
						indexid = Math.min(read.indexOf('*', k), read.indexOf('|', k));
						if(indexid == -1){
							indexid = read.indexOf('|', k);
						}
						try{
						int count = Integer.parseInt(read.substring(k+1, indexid));
//						System.out.print(count +" @ ");
						for(int j = 0;j<count;j++)
							f.incrementCounter(flagfeild);
						k = indexid;
						}
						catch(Exception e){e.printStackTrace();
							System.out.println("EXCEPTION : "+e.getMessage());
						}
					}
				}
				freqCount.add(f);
			}
			else{
				word = word+read.charAt(k);
				
			}
			
		}

	}

	


}
