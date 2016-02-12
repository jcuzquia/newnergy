package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.index;
import views.html.account.signup;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	
	
    public Result index() {
        return ok(index.render("Your Newnergy"));
    }
    
    @Restrict(@Group(Application.USER_ROLE))
    public Result profile(){
    	return TODO;
    }
    
    
   public Result signup(){
	   return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
   }
   
   @Restrict(@Group(Application.USER_ROLE))
   public static User getLocalUser(final Session session){
	   final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
	   final User localUser = User.findByAuthUserIdentity(currentAuthUser);
	   return localUser;
   }
   
   
   public Result doSignup(){
	   com.feth.play.module.pa.controllers.Authenticate.noCache(response());
	   final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
	   
	   if(filledForm.hasErrors()){
		   // User did not fill everything properly
		   return badRequest(signup.render(filledForm));
	   } else {
		   //Everything was filled
		   //do something with your part of the form before handling the user
		   //signup
		   return UsernamePasswordAuthProvider.handleSignup(ctx());
	   }
	   
   }
    
   public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

   public Result jsRoutes() {
	   System.out.println("Application.jsRoutes");
		return ok(
				Routes.javascriptRouter("jsRoutes",
						controllers.routes.javascript.Signup.unverified()))
				.as("text/javascript");
	}

}
