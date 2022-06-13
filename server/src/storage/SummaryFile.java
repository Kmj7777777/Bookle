package storage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SummaryFile {
	private InputStream is = null;
	
	public SummaryFile(String id) {
		is = this.getClass().getResourceAsStream("/bookSummary/" + id + ".txt");
		
		if(is == null) {
			is = this.getClass().getResourceAsStream("/bookSummary/not_found.txt");
		}
	}
	
	public long getFileLength() {
		long length = 0;
		
		try {
			length = is.available();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return length;
	}
	
	public byte[] getSummaryFile() {
		byte[] file = null;
		
		try {
			long fileSize = getFileLength();
			
			DataInputStream dis = new DataInputStream(is);
			file = new byte[(int)fileSize];
			dis.readFully(file);
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
}