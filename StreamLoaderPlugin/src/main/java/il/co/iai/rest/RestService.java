package il.co.iai.rest;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Update;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.Category;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.CategoryService;

import il.co.iai.capture.Config;



@Path("/plugins/iai/stream")
@RequestScoped
//@OperationsInside
@Interceptors({DynamicResourceInjector.class})
@PluginInfo(version=1)
public class RestService implements RestServicePlugin{
	
	@Inject
	Logger logger;
	
	@Inject
	private CategoryService categoryService;
	
	
	

	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@Update
     public void setCategory(@HeaderParam("authenticationkey") String authenticationkey,@PathParam("id")String id,@HeaderParam("version") String version,@Context SecurityContext securityContext) {
	
		Category cat=categoryService.get(id);
		if(cat!=null){
			Config.currentCategory=id;
		}
       
  	}
	
	
	
	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

}
