package il.co.iai.capture;

import java.util.concurrent.atomic.AtomicBoolean;

public class Config {
	public final static long STREAM_TIME_SEPERATOR=3000;
	public final static int BUFFER_SIZE=90000; 
	public final static String OUTPUT_FOLDER="C:/temp/capture";
	public static AtomicBoolean broadcastStarted=new AtomicBoolean(false);
	public final static String CATEGORY_KEY="categoryId";
	public static String currentCategory=null;

}
