package controllers;

import java.io.File;

import play.mvc.Controller;
import play.mvc.Result;
import util.MeterFileReader;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

public class DashboardController extends Controller {

	public static final String DASHBOARD_FLASH_ERROR_KEY = "dashboard error";
	public static final String DASHBOARD_FLASH_MESSAGE_KEY = "dashboard message";
	
	/**
	 * Handling the raw meter data upload
	 * 
	 * @return
	 */
	public Result uploadMeter() {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart meter = body.getFile("meter_data");
		if (meter != null) {
			String fileName = meter.getFilename();
			String contentType = meter.getContentType();
			File file = meter.getFile();
			
			flash(DASHBOARD_FLASH_MESSAGE_KEY, "File Uploaded Succesfully");
			MeterFileReader.readFile(file);
			return redirect(routes.Application.dashboard());
		} else {
			flash(DASHBOARD_FLASH_ERROR_KEY, "Missing file");
			return redirect(routes.Application.dashboard());
		}
	}
	
}
