package il.co.iai.rest;

import java.util.List;
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
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.Category;
import com.flexicore.model.Operation;
import com.flexicore.model.Tenant;
import com.flexicore.model.User;
import com.flexicore.model.media.Media;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassService;
import com.flexicore.service.CategoryService;
import com.flexicore.service.UserService;

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
	
	@Inject
	private BaseclassService baseclassService;
	
	
	
	

	
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
	
	@GET
	@Path("/media")
    @Produces(MediaType.APPLICATION_JSON)
	@Read
     public List<Category> getAllCategories(@HeaderParam("authenticationkey") String authenticationkey,@HeaderParam("version") String version,@Context SecurityContext securityContext) {
		
		User us=baseclassService.find(User.class, "UEKbB6XlQhKOtjziJoUQ8w");
		Tenant tn=baseclassService.find(Tenant.class, "jgV8M9d0Qd6owkPPFrbWIQ");
		Operation o=baseclassService.find(Operation.class, "818c9d6973784a16b488c6");
		SecurityContext sec= new SecurityContext();
		sec.setUser(us);
		sec.setTenant(tn);
		sec.setOperation(o);
		return categoryService.getAllowedCategories(Media.class, null, sec);
       
  	}
	
	
	
	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

}
