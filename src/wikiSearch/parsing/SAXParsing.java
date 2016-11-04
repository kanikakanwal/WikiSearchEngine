package wikiSearch.parsing;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;




public class SAXParsing {
	
	
	public static String source, sourceparent;
	private long fileCount = 0;
	BufferedWriter brmap;
	String parentmap;

	public long parseMethod(String sourcename, String myFolder)
	{
//		System.out.println("hoiii");
		source = sourcename;	
		sourceparent = myFolder;
		parentmap = sourceparent + "//mapIdTitle";
			
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
//			System.out.println("yes");
			MyHandler mh = new MyHandler(parentmap,sourceparent);
			mh.initialise();
			saxParser.parse(source, mh);
			fileCount = mh.execute();
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
			   System.out.println("EXCEPTION : "+e.getMessage());
		}
		catch(IOException e){
			e.printStackTrace();
			   System.out.println("EXCEPTION : "+e.getMessage());
		}
		catch (SAXParseException e) 
        {
			e.printStackTrace();
           System.out.println("EXCEPTION : "+e.getMessage());
        }
        catch (SAXException e) 
        {
        	e.printStackTrace();
           System.out.println("EXCEPTION : "+e.getMessage());
        }  
		catch(Exception e){
			e.printStackTrace();
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		
		return fileCount;
	}


		
}





