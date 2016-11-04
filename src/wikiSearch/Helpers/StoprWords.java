package wikiSearch.Helpers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class StoprWords {
	


	private Set<String> stopwordsmap;
	
	public StoprWords()
	{
		stopwordsmap = new HashSet<String>();
		InputStream is;
		BufferedReader br;
		String read;

		try{
			is = new FileInputStream("stopwords.txt");
			br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			while ((read = br.readLine()) != null) {
				stopwordsmap.add(read);
			}
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		br = null;
		is = null;		
	}
	
	public boolean isStopWords(StringBuilder str){		
		return (!stopwordsmap.contains(str));		
	}
	public boolean isStopWords(String str){		
		return (!stopwordsmap.contains(str));		
	}
	


}
