package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.Project;
import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.twirl.api.Html;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.about;
import views.html.contact;
import views.html.dashboard_main;
import views.html.index;
import views.html.profile;
import views.html.signup;
import views.html.account.login;
import views.html.dashboard.my_projects;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	/**
	 * Shows the home page
	 * @return
	 */
	public Result index() {
		return ok(index.render(Const.NAV_HOME));
	}

	/**
	 * Show profile page. This is restricted and can only be accessed once authenticated
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result profile() {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session());
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		Html html = profile.render(localUser);
		return ok(html);
	}

	/**
	 * Show the about page
	 * @return
	 */
	public Result about() {
		Html html = about.render(Const.NAV_ABOUT);
		return ok(html);
	}

	/**
	 * Shows the contact information page
	 * @return
	 */
	public Result contact() {
		Html html = contact.render(Const.NAV_CONTACT);
		return ok(html);
	}

	/**
	 * shows the signup page
	 * @return Result
	 */
	
	public Result signup() {
		Html html = signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM);
		return ok(html);
	}

	/**
	 * Gets the local user
	 * @param session
	 * @return
	 */
	public static User getLocalUser(final Session session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		return localUser;
	}

	/**
	 * Shows the dashboard page. 
	 * @param mode applicable to the nav bar
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result dashboard(String mode) {
		
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session());
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		List<Project> projects = Project.findAllByUser(localUser.email);
		
		return ok(dashboard_main.render(localUser, 
				Const.NAV_DASHBOARD,
				my_projects.render(projects)
					)
				);
		
	}

	public Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();

		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup

			return UsernamePasswordAuthProvider.handleSignup(ctx());
		}

	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

	public Result jsRoutes() {
		return ok(Routes.javascriptRouter("jsRoutes", controllers.routes.javascript.Signup.unverified()))
				.as("text/javascript");
	}

	public Result login() {
		return ok(views.html.account.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	public Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(login.render(filledForm));
		} else {
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

}
