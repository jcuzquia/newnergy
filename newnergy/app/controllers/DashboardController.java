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
	
	
	
	
}
