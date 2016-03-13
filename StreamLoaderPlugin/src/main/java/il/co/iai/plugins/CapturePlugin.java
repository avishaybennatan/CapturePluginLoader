package il.co.iai.plugins;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.enums.DefaultFileTypes;
import com.flexicore.model.Category;
import com.flexicore.model.FileResource;
import com.flexicore.model.FileType;
import com.flexicore.model.Job;
import com.flexicore.model.MediaToBundle;
import com.flexicore.model.Result;
import com.flexicore.model.User;
import com.flexicore.model.media.Media;
import com.flexicore.model.rendering.presets.VideoMetadata;
import com.flexicore.service.BaseclassService;
import com.flexicore.service.CategoryService;
import com.flexicore.service.MediaService;
import com.flexicore.service.UserService;

import il.co.iai.capture.CaptureJob;
import il.co.iai.capture.CaptureRunner;
import il.co.iai.capture.Config;
import il.co.iai.interfaces.StreamCapturePlugin;
import tv.goopi.plugin.VideoInfo;
import tv.goopi.plugin.VideoSnapShot;

@PluginInfo(version = 1, autoInstansiate = true)
public class CapturePlugin implements StreamCapturePlugin {

	@Resource(name = "DefaultManagedExecutorService")
	ManagedExecutorService executor;
	@Inject
	@PluginInfo(version = 1)
	private CaptureRunner captureRunner;
	@Inject
	private Logger logger;
	@Inject
	private CategoryService categoryService;
	@Inject
	private MediaService mediaService;
	@Inject
	private BaseclassService userService;

	@Override
	public void process(Job job) {
		if (job.getJobInformation().getJobInfo() instanceof CaptureJob) {
			try {
				CaptureJob captureJob = (CaptureJob) job.getJobInformation().getJobInfo();
				User user = userService.find(User.class,"UEKbB6XlQhKOtjziJoUQ8w");
				Media media = Media.s().Create("capture media", user);
				media.Init();
				FileResource fileResource;

				fileResource = FileResource.s().Create("capture File", user);

				fileResource.Init();
				fileResource.setFullPath(captureJob.getFilePath());
				fileResource.setType(new FileType(DefaultFileTypes.VIDEO.name()));
				FileResource snapshot =null;
				try {
					VideoMetadata m=VideoInfo.getVideoMetadata(fileResource.getFullPath(), job);
					fileResource.setMetaData(m);
					String snap=VideoSnapShot.Create(fileResource.getFullPath(), "png", user);
					snapshot=FileResource.s().Create("capture snap", user);

					snapshot.Init();
					snapshot.setFullPath(snap);
					snapshot.setType(new FileType(DefaultFileTypes.IMAGE.name()));
					job.addObjectToPresist(snapshot);
					job.addObjectToPresist(m);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MediaToBundle link = MediaService.staticAddNewFileResourceToMedia(media, fileResource, "capture");
				media.setPrimaryFileResourceBundle(link.getRightside());
				link.getRightside().setType(new FileType(DefaultFileTypes.VIDEO.name()));
				Properties props=job.getJobInformation().getJobProperties();
				mediaService.update(media);
				if(props!=null){
					String id=props.getProperty(Config.CATEGORY_KEY,null);
					if(id!=null){
						categoryService.connectCategory(media.getId(), id, user);
					}
				}
				if(snapshot!=null){
					media.setSnapshotResourceId(snapshot.getId());
				}
				job.addObjectToPresist(media);
				Result res= Result.s().Create("result", user);
				res.Init();
				res.setSucceeded(true);
				job.getJobInformation().setCurrrentPhaseResult(res);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.log(Level.SEVERE, "unable to instanciate", e);
			}

		}

	}

	@Override
	public void init() {
		if(!Config.broadcastStarted.getAndSet(true)){
			executor.execute(captureRunner);
		}
		

	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOrder(Job job) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

}
