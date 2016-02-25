package controllers;

import java.util.List;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Project;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.dashboard.project_info;

public class ProjectController extends Controller {

	
	private static final Form<Project> PROJECT_FORM = Form.form(Project.class);
	/**
	 * Displays a list of project in which the user is part of
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result list() {
		return TODO;
	}
	
	@Restrict(@Group(Application.USER_ROLE))
	public Result addProject(){
		
		return ok(project_info.render(PROJECT_FORM));
	}
	
	public Result saveProject(){
		Form<Project> ourForm = Form.form(Project.class).bindFromRequest();
		if(ourForm.hasErrors()){
			return badRequest(project_info.render(PROJECT_FORM));
		}
		Project project = ourForm.get();
		
		if(project != null){
			project.save();
			return redirect(routes.Application.dashboard(""));
		}
		return badRequest(project_info.render(PROJECT_FORM));
	}
}
