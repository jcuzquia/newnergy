package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import models.Data;
import models.Project;
import models.User;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.Array;
import play.mvc.Result;
import util.MeterFileReader;
import views.html.dashboard_main;
import views.html.dashboard.controller;
import views.html.dashboard.my_projects;

public class DashboardController extends Controller {

	public static final String DASHBOARD_FLASH_ERROR_KEY = "dashboard error";
	public static final String DASHBOARD_FLASH_MESSAGE_KEY = "dashboard message";
	
	/**
	 * Handling the raw meter data upload
	 * 
	 * @return
	 */
	public Result getMeterData(String mode) {
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("meter_data");
		List<Data> dataList = new ArrayList<Data>();
		if(filePart != null) {
			File file = filePart.getFile();
			dataList = MeterFileReader.getDataListFromFile(file);
			final AuthUser currentAuthUser = PlayAuthenticate.getUser(session());
			final User localUser = User.findByAuthUserIdentity(currentAuthUser);
			
			flash("Message", "Upload Successful");
			
			return ok(dashboard_main.render(localUser, 
					"", 
					controller.render("Controller", dataList),
					my_projects.render(Project.findAllByUser(localUser.email))));
			
		} else {
			flash ("Error", "failed uploading file");
			return redirect(routes.Application.dashboard(mode));
		}
		
	}
	
	public Result jsDashboardRoutes() {
		response().setContentType("text/javascript");
		return ok(
				Routes.javascriptRouter("jsDashboardRoutes", 
				// Routes
				routes.javascript.DashboardController.getMeterData()));
	}
	
	
}
