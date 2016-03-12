package il.co.iai.capture;

import java.io.Serializable;

public class CaptureJob implements Serializable{
	private String filePath;
	
	
	

	public CaptureJob(String filePath) {
		super();
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
