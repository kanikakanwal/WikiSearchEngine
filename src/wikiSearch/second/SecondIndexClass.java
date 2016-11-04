package wikiSearch.second;

public class SecondIndexClass {
	private String entry;
	private long offset;
	
	public SecondIndexClass(){
		entry = new String();
		offset = -1;
	}
	public SecondIndexClass(String str, long off){
		entry = str;
		offset = off;
	}
	
	public String getKey(){
		return entry;
	}
	public long getOffset(){
		return offset;
	}
}
