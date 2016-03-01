package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.Data;
import models.Meter;
import models.MeterForm;
import models.Project;
import models.ProjectForm;
import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import util.MeterFileReader;
import views.html.dashboard_main;
import views.html.dashboard.controller;
import views.html.dashboard.my_projects;
import views.html.dashboard.project_info;
import views.html.project.project_page;
import views.html.project.meter_info;
import views.html.project.meter_list;

public class ProjectController extends Controller {

	private static final Form<ProjectForm> PROJECT_FORM = Form.form(ProjectForm.class);
	private static final Form<MeterForm> METER_FORM = Form.form(MeterForm.class);
	
	/**
	 * Displays a list of project in which the user is part of
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result list() {
		return TODO;
	}
	
	/**
	 *  Show project page. This project page has my meter_list,
	 *  Has the Charts Page
	 * @param id
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result showProject(Long id){
		Project project = Project.findById(id);
		if(project != null){
			return ok(project_page.render(project,
					Const.MY_METERS, 
					meter_list.render(project)));
		} else {
			return badRequest();
		}
		
	}
	
	@Restrict(@Group(Application.USER_ROLE))
	public Result addProject(){
		
		return ok(project_info.render(PROJECT_FORM));
	}
	
	public Result saveProject(){
		Form<ProjectForm> projectForm = Form.form(ProjectForm.class).bindFromRequest();
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session());
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		
		if(projectForm.hasErrors()){
			flash("error", "Form has errors");
			return badRequest(project_info.render(PROJECT_FORM));
		}
		String title = projectForm.get().title;
		String description = projectForm.get().description;
		Project project = Project.create(localUser,title,description);
		
		if(project != null){
			localUser.projects.add(project);
			localUser.save();
			return redirect(routes.Application.dashboard(""));
		}
		return badRequest(project_info.render(PROJECT_FORM));
	}
	
	public Result deleteProject(Long id){
		Project project = Project.findById(id);
		if(project == null) {
			badRequest("Project not found");
		}
		project.delete();
		return redirect(routes.Application.dashboard(Const.NAV_DASHBOARD));
	}
	
	/**
	 * Handling the raw meter data upload
	 * 
	 * @return
	 */
	public Result getMeterData(String mode, Long projectId) {
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("meter_data");

		final Project project = Project.findById(projectId);
		
		List<Data> dataList = new ArrayList<Data>();
		if(filePart != null) {
			
			File file = filePart.getFile();
			dataList = MeterFileReader.getDataListFromFile(file);
			
			//if data list is not empty then create a meter instance
			if(!dataList.isEmpty()){
				Meter meter = new Meter(dataList, project);
				
				project.addMeter(meter);
				meter.save();
			}
						
			flash("Message", "Upload Successful");
			
			return redirect(routes.ProjectController.showProject(project.id));
			
		} else {
			flash ("Error", "failed uploading file");
			return redirect(routes.ProjectController.addMeter(projectId));
		}
		
	}
	
	public Result showMeterHeatMap(Long id){
		Meter meter = Meter.findById(id);
		Project project = meter.project;
		return ok(project_page.render(project, 
				Const.METER_HEAT_MAP,
				meter_list.render(project)));
	}
	public Result addMeter(Long projectId){
		Project project = Project.findById(projectId);
		return ok(meter_info.render(METER_FORM, project));
	}
	
	public Result jsProjectRoutes() {
		response().setContentType("text/javascript");
		return ok(
				Routes.javascriptRouter("jsProjectRoutes", 
				// Routes
				routes.javascript.ProjectController.getMeterData()));
	}
	
	
	public Result deleteMeter(Long id){
		Meter meter = Meter.findById(id);
		Project project = meter.project;
		meter.delete();
		return redirect(routes.ProjectController.showProject(project.id));
	}
}
