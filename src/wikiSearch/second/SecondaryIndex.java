package wikiSearch.second;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SecondaryIndex {

	private FileInputStream fs;
	private DataInputStream in;
	private BufferedReader br;
	private BufferedWriter bw;
		
	
	public void CreateSecondary(String myFolder, int op) throws IOException
	{
		StringBuilder body;
		String read,word;
		final int lap = 24;
		int countingLap = 0;
		long totalBytes = 0;
		int ind;
		if(op == 1)
			fs = new FileInputStream(myFolder+"/finalindex");
		else
			fs = new FileInputStream(myFolder+"/mapIdTitle");

		in = new DataInputStream(fs);
		br = new BufferedReader(new InputStreamReader(in));
		if(op == 1)
		{
			bw = new BufferedWriter(new FileWriter(myFolder+"/secondaryIndex"));
		}
		else
		{
			bw = new BufferedWriter(new FileWriter(myFolder+"/secondIdTitleIndex"));

		}
		body = new StringBuilder("");
		
			while((read = br.readLine())!=null){
				if(countingLap == 0)
				{
					ind = read.indexOf(':');
					if(ind==-1)
					{
//						System.out.println(ind+"id");
//						System.out.println("word"+read.substring(0, ind));
						continue;
					}
					word = read.substring(0, ind);
//					System.out.println("word:"+word);
					body = body.append(word+":"+totalBytes+"\n");
					if(body.length() > 499){
		            	 bw.write(body.toString());
		            	 body = new StringBuilder("");		            	 
		             }
					countingLap = lap;
				}
				else
				{
					countingLap--;
				}
				totalBytes += read.getBytes().length;
				totalBytes++;
				
				
			}
			if(body.length() != 0){
				 bw.write(body.toString());
			}
			br.close();
			bw.close();
	
	}

}
