package wikiSearch.parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import wikiSearch.Helpers.CountFrequency;
import wikiSearch.Helpers.Stemmer;
import wikiSearch.Helpers.StoprWords;
import wikiSearch.parsing.SAXParsing;

public class MyHandler extends DefaultHandler {


	boolean flgid = false;
	boolean flgtext = false;
	boolean flgvar = false;
	boolean flgpage = false;
	boolean flgtitle = false;
	public static StringBuilder mapidtitle = new StringBuilder();
	private long fileCount = 0;
	BufferedWriter brmap;
	long pgnum=0;
	boolean flag = false;
	String sourcefile;
	int flagfield;
	StringBuilder sbtitle,sbid,sbtext;
	String docid;
	Map<String, ArrayList<CountFrequency>> index;
	File filemap;
	String parentmap;
	Path writetoFile,idTitlemap;
	StoprWords stopWords;
	
	Stemmer stem;
	public static String sourceparent;
	
	
	
	MyHandler(String parentmap1,String sourceparent1){
//		System.out.println("const");
		
		
		sourceparent = sourceparent1;
		parentmap = parentmap1;
		
		
	}
	
	public void initialise()
	{
		filemap = new File(parentmap);
//		System.out.println("file:"+filemap);
		if(!filemap.exists()){
			try {
				filemap.createNewFile();
			} 
			catch (IOException e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				System.out.println("EXCEPTION : "+e.getMessage());
			}
		}
		try{
			
			brmap = new BufferedWriter(new FileWriter(filemap));
			

		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName,String qName, 
             Attributes attributes) throws SAXException {

	if (qName.equalsIgnoreCase("MEDIAWIKI") || qName.equalsIgnoreCase("FILE")) {
		System.out.println("start");
		index = new TreeMap<String, ArrayList<CountFrequency>>();
		
		stopWords = new StoprWords();
		stem = new Stemmer();
		
		sourcefile = SAXParsing.source;
	}
	if (qName.equalsIgnoreCase("PAGE")) {
		pgnum++;
		flgpage = true;
	}
 		
	if (qName.equalsIgnoreCase("TITLE")) {
		flgtitle = true;
		sbtitle = new StringBuilder();
	}

	if (qName.equalsIgnoreCase("ID")) {
		flgid = true;
		sbid =  new StringBuilder();
	}

	if (qName.equalsIgnoreCase("TEXT")) {
		flgtext = true;
		flagfield = 3;
		sbtext = new StringBuilder();
	}
	}

public void endElement(String uri, String localName,
	String qName) throws SAXException {
	
	if(qName.equalsIgnoreCase("PAGE")){
		if(pgnum % 7000 == 0)
		{
			writeFile();
		}
		flagfield = 0;
		flag = false;
		flgpage = false;
		
	}
	if(qName.equalsIgnoreCase("TITLE")){
		flgtitle = false;
		if(sbtitle.length() > 10)
			if(sbtitle.substring(0, 10).equalsIgnoreCase("Wikipedia:")){
				flgvar = false;
		}
	}
	if(qName.equalsIgnoreCase("ID") && flag == false){
		docid = new String(sbid.toString());
		flag = true;
		flgid = false;
		try{
			if(flgvar)
				brmap.write(docid+":"+sbtitle+"\n");
		
		}
		catch(Exception e){
			try {
				brmap.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(mapidtitle.length() == 10000){
			try {
				brmap.write(mapidtitle.toString());
				mapidtitle.setLength(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	if(qName.equalsIgnoreCase("TEXT")){
		
		flgtitle = true;
		if(flgvar)
			internalParse(sbtitle);
		flgtitle = false;
		
		
		flgtext = false;
		
	}
	if(qName.equalsIgnoreCase("MEDIAWIKI") || qName.equalsIgnoreCase("FILE")){
		
		if(mapidtitle.length() != 0 ){
			try {
				brmap.write(mapidtitle.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			brmap.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!index.isEmpty())
		{
//			System.out.println("writing");
			writeFile();
		}
		System.out.println("Num of pages : "+pgnum);	
							
	}
	//System.out.println("End Element :" + qName);

}

public void characters(char ch[], int start, int length) throws SAXException {
	 
//	System.out.println("char");
	if (flgtitle) {
		sbtitle.append(ch,start,length);
		flgvar = true;
		
	}

	if (flgid) {
		sbid.append(ch,start,length);
	}

	
	if (flgtext && flgvar) {
			
			internalParse(ch, start,length);
			

			return;
		
	}	 				
}

private void internalParse(char[] charr, int start, int length){
	// TODO Auto-generated method stub
	String temp = new String();
	temp = "";
	char c1='{';
	char c2 = '{';
	String prevChar = new String();
	prevChar = "";
	Stack<Character> brac = new Stack<Character>();
	if(flgtitle == true)
		flagfield = 1;
	
	for(int i = start;(i+start)<length;i++){
		
		//System.out.print(stemp.charAt(i));
		char ch = charr[i];
		if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
		{
			temp = temp + Character.toLowerCase(ch);
			
			
			if((i+2 < length)  && (flgtitle == false)){
				if(temp.equals("infobox") && (c1=='{') && (c2=='{') )
					flagfield = 2;
				if(temp.equals("references") && (c1 == '=') && (c2 == '=') && (charr[i+1] == '=') && (charr[i+2] == '='))
				{ temp = "";	flagfield = 6;}
				if(temp.equals("links") && prevChar.equals("external") && (c1 == '=') && (c2 == '=') && (charr[i+1] == '=') && (charr[i+2] == '='))
				{temp = "";	flagfield = 5;}
				if(temp.equals("category") && (c1 == '[') && (c2 == '[') && (charr[i+1] == ':'))
				{
					temp = "";	flagfield = 4;
				}
				if(temp.length()==2 && (c1 == '[') && (c2 == '[') && (charr[i+1] == ':'))
				{
					temp = "";	flagfield = 7;
				}
				
			}
		}
		else{
			if(ch != ' '){
				c2 = c1;
				c1 = ch;
			}
			if(ch == '{'){
				brac.push(ch);							
			}
			if(ch == '}'){
				if(!brac.empty())
					brac.pop();
				if(brac.empty() && flagfield == 2){
					flagfield = 3;								
				}
			}
			if((flagfield == 7 || flagfield == 4) && c1 ==']' && c2 ==']'){
				flagfield = 3;
			}
			if(flagfield == 5 && c1 =='{' && c2 =='{'){
				flagfield = 3;
			}
			if(!stopWords.isStopWords(temp)){
				temp = "";
				continue;
			}
			if(!temp.isEmpty()  ){
				prevChar = temp;
				if(flagfield != 0)
					operateKeyword(temp, flagfield);
				temp = "";
			}						
		}
		
						
	}
	
	if(!temp.isEmpty()  ){
//		prevChar = temp;
		if(flagfield != 0)
			operateKeyword(temp, flagfield);
		temp = "";
	}

}

private void internalParse(StringBuilder stemp)
{

	// TODO Auto-generated method stub
	String temp = new String();
	temp = "";
	//flagfield = 0;
	

	
	if(flgtitle == true)
		flagfield = 1;
	
	for(int i = 0;i<stemp.length();i++){
		
//		System.out.print("######### : "+stemp.charAt(i));
		char ch = stemp.charAt(i);
		if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
		{
			temp = temp + Character.toLowerCase(ch);
								
		}
		
		else{
			
			if(flagfield != 0)
//				System.out.println("temp>>"+temp);
				operateKeyword(temp, flagfield);
			temp = "";
		}
						
	}
	
	if(!temp.isEmpty()){
		if(flagfield != 0)
//			System.out.println("temp>>"+temp);
			operateKeyword(temp, flagfield);
		temp = "";
	}

}



private void writeFile() {
	// TODO Auto-generated method stub
	fileCount++;
	File file = new File(sourceparent);
	String temp;
	if(!file.exists())
		file.mkdirs();
	temp = sourceparent + "/file_" + fileCount;
	file = new File(temp);
//	System.out.println(temp + " : ");
	
	
		try {
			file.createNewFile();
		} 
		catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		fileWrite(file);
		index = new TreeMap<String, ArrayList<CountFrequency>>();
}

	private void fileWrite(File file) {
	// TODO Auto-generated method stub
	
	StringBuilder content = new StringBuilder();
	//Set set = index.entrySet();
    Iterator<Entry<String, ArrayList<CountFrequency>>> i = index.entrySet().iterator();
   // System.out.println("hiii");
    
    try{
    	FileWriter fw = new FileWriter(file.getAbsoluteFile());
//    	System.out.println("%%"+file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
        BufferedWriter br = new BufferedWriter(fw);
        
		int previd = 0;
		//float size = 0;
    while(i.hasNext()) {
 		Entry<String, ArrayList<CountFrequency>> me = i.next();
         content.append(me.getKey() + ":");
         
         previd = 0;
         ArrayList<CountFrequency> fc = me.getValue();
         content.append(fc.size() + ":");
         for(CountFrequency f : fc){
        	 
        	 content.append(Integer.parseInt(f.wikiId())-previd);
        	 previd = Integer.parseInt(f.wikiId());
        	 if(f.wikiTitle() != 0){
        		 content.append("*t"+f.wikiTitle());
        	 }
        	 if(f.wikiInfo() != 0){
        		 content.append("*i"+f.wikiInfo());
        	 }
        	 if(f.wikiBody() != 0){
        		 content.append("*b"+f.wikiBody());
        	 }
        	 if(f.wikiReferences() != 0){
        		 content.append("*r"+f.wikiReferences());
        	 }
        	 if(f.wikiLink() != 0){
        		 content.append("*L"+f.wikiLink());
        	 }
        	 if(f.wikiCategory() != 0){
        		 content.append("*c"+f.wikiCategory());
        	 }
        	 if(f.wikiinlinks() != 0){
        		 content.append("*l"+f.wikiinlinks());
        	 }
        	 content.append("|");
        	 //System.out.print(f.getId() + " -  T " + f.getTitle() + " |  I " + f.getInfo()+" | B " + f.getBody()+" | R " + f.getreferences()
        	//		 +" | L " + f.getLink() +" | C " + f.getCategory() + " #### ");				        	
         }
         content.append("\n");
         if(content.length() > 1000){
//        	 System.out.println("%%%%%%%%%%%%%%%%");
        	// size += content.length();
        	 //bw.write(content.toString());
        	 br.write(content.toString());
        	 content = new StringBuilder("");
        	 
         }
//         //System.out.println();
         
      }
        if(content.length() != 0)
        	//bw.write(content.toString());
        {
        	//size += content.length();	 
        	br.write(content.toString());}
        	//System.out.println("  :  "+(size/(1024*1024)) + " MB");
        br.close();
		bw.close();
      }
      catch(IOException e){
    	  e.printStackTrace();
    	  System.out.println("EXCEPTION : "+e.getMessage());
      }		
}

private void operateKeyword(String temp, int flag){
	//if(temp.charAt(0) == '?')return;
	
	stem.add(temp.toCharArray(), temp.length());
	stem.stem();
	temp = stem.toString();
	if(stopWords.isStopWords(temp)||flag==1){
		if(!index.containsKey(temp)){
			CountFrequency cf = new CountFrequency(docid);
			cf.incrementCounter(flag);
			index.put(temp, new ArrayList<CountFrequency>());
			index.get(temp).add(cf);
		}
		else
		{
			ArrayList<CountFrequency> tlist = index.get(temp);
			int flag1 = 0;
			int size = tlist.size()-1;
			if(tlist.get(size).wikiId().equalsIgnoreCase(docid)){
				index.get(temp).get(size).incrementCounter(flag);								
				flag1 = 1;
			}										
			if(flag1 == 0){
				CountFrequency cf = new CountFrequency(docid);
				cf.incrementCounter(flag);
				index.get(temp).add(cf);
			}
		}
	}
}

public long execute(){
	return fileCount;
}
	
}
