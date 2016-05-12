package controllers;

import java.util.ArrayList;
import java.util.List;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.Data;
import models.Meter;
import models.Project;
import models.TimeSeriesData;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import util.HtmlFactory;

/**
 * Control the time series page of the meter
 * @author Uzquianoj1
 *
 */
public class TimeSeriesController extends Controller{

	public Result showTimeSeriesPage(Long meterId){
		String key = Const.TIME_SERIES;
		Meter meter = Meter.findById(meterId);
		if (meter == null){
			return badRequest();
		}
		Project project = meter.project;
		Html dailyTimeSeriesPage = HtmlFactory.getPage(project, meter, key);
		return ok(dailyTimeSeriesPage);
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
		return redirect(routes.TimeSeriesController.showTimeSeriesPage(id));
	}
	
	public Result getTimeSeriesJson(String meterId){
		System.out.println("Calling the method in the controller");
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
		
		for(TimeSeriesData tsd : tsdList){
			csv.add(tsd.getCSVString());
		}
		return ok(Json.toJson(csv));
	}
	
	public Result jsTimeSeriesRoutes(){
		System.out.println("Activating the jsTimeSeriesRoutes");
		response().setContentType("text/javascript");
		return ok(
				Routes.javascriptRouter("jsTimeSeriesRoutes", 
				// Routes
				routes.javascript.TimeSeriesController.getTimeSeriesJson(),
				routes.javascript.TimeSeriesController.activateDayType()));
	}
}
