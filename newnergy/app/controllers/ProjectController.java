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
import models.TimeSeriesData;
import models.User;
import play.Routes;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import util.HtmlFactory;
import util.MeterFileReader;
import views.html.dashboard.project_info;
import views.html.project.meter_info;


/**
 * Controls the project page
 * @author Camilo Uzquiano
 *
 */
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
	 *  Show project page. This project page has my meter_list html,
	 * @param id of the project
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result showProject(Long projectId, Long meterId){
		String key = Const.PROJECT_PAGE;
		Project project = Project.findById(projectId);
		Meter meter = Meter.findById(meterId);
		
		
		if(project != null){
			Html projectPage = HtmlFactory.getPage(project, meter, key);
			return ok(projectPage);
		} else {
			return badRequest();
		}
		
	}
	
	/**
	 * Show the heat map of the meter
	 * @param id of the meter
	 * @return
	 */
	public Result showMeterPage(Long meterId){
		String key = Const.METER_PAGE;
		Meter meter = Meter.findById(meterId);
		Project project = meter.project;
		if(meterId != null){ //never suppossed to be 0
			Html heatMapPage = HtmlFactory.getPage(project, meter, key);
			return ok(heatMapPage);
		} else {
			return badRequest();
		}
	}
	
	
	
	/**
	 * Shows the form that will be used to add a project
	 * @return
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public Result addProject(){
		
		return ok(project_info.render(PROJECT_FORM));
	}
	
	/**
	 * Sends the request to save the new project into the database
	 * @return
	 */
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
	
	/**
	 * Deletes the project from the database
	 * @param id
	 * @return
	 */
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
		
		//working with the form
		Form<Meter> meterForm = Form.form(Meter.class).bindFromRequest();
		if(meterForm.hasErrors()){
			flash("Error", "The meter form has errors");
			return badRequest("Meter Form has errors");
		}
		
		Meter meter = meterForm.get();
		//getting the file data
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("meter_data");

		Project project = Project.findById(projectId);
		
		List<Data> dataList = new ArrayList<Data>();
		if(filePart != null) {
			
			File file = filePart.getFile();
			dataList = MeterFileReader.getDataListFromFile(file);
			//if data list is not empty then create a meter instance
			if(!dataList.isEmpty()){
				meter.setDataList(dataList);
				project.addMeter(meter);
				project.update();
				meter.save();
			}
			flash("Message", "Upload Successful");
			return redirect(routes.ProjectController.showProject(project.id, meter.id));
			
		} else {
			flash ("Error", "failed uploading file");
			return redirect(routes.ProjectController.showProject(projectId, 0));
		}
		
	}
	
	public Result addMeter(Long projectId){
		Project project = Project.findById(projectId);
		return ok(meter_info.render(METER_FORM, project));
	}
	
	
	/**
	 * This is called from the javascript method acter clicking a button,
	 * from the meter list
	 * @param meterId
	 * @return
	 */
	@SuppressWarnings("unused")
	public Result retrieveHeatmapData(Long meterId){
		Meter meter = Meter.findById(meterId);
		Long projectId = meter.project.id;
		
		return redirect(routes.ProjectController.showProject(projectId, meterId));
	}
	
	/**
	 * Finds and deletes an specific meter
	 * @param id
	 * @return
	 */
	public Result deleteMeter(Long id){
		Meter meter = Meter.findById(id);
		Project project = meter.project;
		meter.delete();
		return redirect(routes.ProjectController.showProject(project.id, 0));
	}
	
	/**
	 * Show the daily time series and schedule
	 * @param id of Meter
	 * @return
	 */
	public Result showDailyTimeSeries(Long meterId){
		String key = Const.TIME_SERIES;
		Meter meter = Meter.findById(meterId);
		if (meter == null){
			return badRequest();
		}
		Project project = meter.project;
		Html dailyTimeSeriesPage = HtmlFactory.getPage(project, meter, key);
		return ok(dailyTimeSeriesPage);
	}
	
	
	public Result getTimeSeriesJson(String meterId){
		Long id = new Long(meterId);
		Meter meter = Meter.findById(id);
		List<TimeSeriesData> tsdList = new ArrayList<TimeSeriesData>(meter.getTimeSeriesData()); 
		List<String> csv= new ArrayList<String>();
		String firstLine = "Categories";
		TimeSeriesData f = tsdList.get(0);
		
		for (Data d: f.getTimeData()){
			firstLine += "," + d.getDayType();
		}
		firstLine += "\n";
		
		csv.add(firstLine);
		
		System.out.println(tsdList.get(0).getMinute());
		for(TimeSeriesData tsd : tsdList){
			csv.add(tsd.getCSVString());
		}
		return ok(Json.toJson(csv));
	}
	
	/**
	 * Routes that are called from javascript code in the template
	 * @return
	 */
	public Result jsProjectRoutes() {
		response().setContentType("text/javascript");
		return ok(
				Routes.javascriptRouter("jsProjectRoutes", 
				// Routes
				routes.javascript.ProjectController.getTimeSeriesJson(),
				routes.javascript.ProjectController.getMeterData(),
				routes.javascript.ProjectController.activateDayType()));
	}
	
	/**
	 * This is called from the list of daytype checkboxes in the time_series_page
	 * @param dayType
	 * @param meterId
	 * @return
	 */
	public Result activateDayType(String dayType, String meterId){
		System.out.println("Activating dayType: " + dayType + " ...Meter: " +meterId);
		Long id = new Long(meterId);
		Meter meter = Meter.findById(id);
		meter.activateDayType(dayType);
		meter.update();
		return redirect(routes.ProjectController.showDailyTimeSeries(id));
	}
}
