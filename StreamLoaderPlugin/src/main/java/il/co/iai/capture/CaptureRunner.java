package il.co.iai.capture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.service.JobService;

import il.co.iai.interfaces.StreamCapturePlugin;

@PluginInfo(version = 1)
public class CaptureRunner implements Runnable, ServicePlugin {
	@Inject
	private Logger logger;

	@Override
	public void run() {

		try {
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking( false );
			 DatagramSocket datagramSocket = channel.socket();
			 datagramSocket.setReuseAddress(true);
			 InetSocketAddress addr=new InetSocketAddress(9999);
			 datagramSocket.bind(addr);
			
			boolean b=datagramSocket.isConnected();
			Selector selector= Selector.open();
			logger.info("capture stream started");
			channel.register(selector, SelectionKey.OP_READ);
			  ByteBuffer buf = ByteBuffer.allocate(Config.BUFFER_SIZE);

			while (true) {
				
					File file = null;
					FileChannel fileChannel = null;
					
					while (true) {
						
						
						int i=selector.select(Config.STREAM_TIME_SEPERATOR);
						if(i==0){
							if(fileChannel!=null){
								fileChannel.close();
								fileChannel=null;
							}
							
							break;
						}
						else{
							Set<SelectionKey> selectedKeys=selector.selectedKeys();
							Iterator<SelectionKey> iter = selectedKeys.iterator();
							while(iter.hasNext()){
								   SelectionKey ky = iter.next();
								   iter.remove();
								   if (!ky.isValid()) {
			                              continue;
			                            }
								   if(ky.isReadable()){
									   
									   if(file==null){
											file=new File(Config.OUTPUT_FOLDER + UUID.randomUUID().toString() + ".mp4");
											fileChannel=new FileOutputStream(file, true).getChannel();
											logger.info("starting capture to: "+file.getAbsolutePath());
										}
									 
										buf.clear();
									   DatagramChannel ch=(DatagramChannel) ky.channel();
									   try{
										 
										   ch.receive(buf);
									   }
									   catch(Exception e){
										   logger.log(Level.SEVERE, "read failed",e);
									   }
					
										buf.flip();
										fileChannel.write(buf);
										
								   }

							}
							
						}
						
						
						

					}
					if(file!=null){
						logger.info("starting CaptureJob for: "+file.getAbsolutePath());
						CaptureJob job = new CaptureJob(file.getAbsolutePath());
						Properties prop= new Properties();
						if(Config.currentCategory!=null){
							prop.setProperty(Config.CATEGORY_KEY, Config.currentCategory);
						}
						
						JobService.startJob(job, StreamCapturePlugin.class, prop, null, null);
						file=null;
					}
				

				
			}
		} catch ( IOException  e) {
			logger.log(Level.SEVERE, "unable to listen", e);
		}

	}

	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

}
