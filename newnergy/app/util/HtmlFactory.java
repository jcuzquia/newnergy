package util;

import constants.Const;
import models.Meter;
import models.Project;
import play.twirl.api.Html;
import views.html.project.heatmap_gui;
import views.html.project.heatmap_page;
import views.html.project.meter_list;
import views.html.project.project_page;
import views.html.project.time_series_gui;

public class HtmlFactory {

	/**
	 * this is
	 * 
	 * @param meter
	 * @param key
	 * @return
	 */
	public static Html getPage(Project project, Meter meter, String key) {
		Html page = null;
		Html meterListPage;
		Html heatmapGui;
		Html timeSeriesGui;
		switch (key) {
		case (Const.PROJECT_PAGE):
			meterListPage = meter_list.render(project);
			heatmapGui = heatmap_gui.render("");
			page = project_page.render(project, Const.MY_METERS, meterListPage, heatmapGui);
			break;
		case (Const.METER_HEAT_MAP):
			meterListPage = meter_list.render(project);
			heatmapGui = heatmap_gui.render("");
			page = heatmap_page.render(project, Const.MY_METERS, meterListPage, heatmapGui,meter);
			break;
		case (Const.TIME_SERIES):
			meterListPage = meter_list.render(project);
			timeSeriesGui = time_series_gui.render(meter);
			page = project_page.render(project, Const.MY_METERS, meterListPage, timeSeriesGui);
			break;
		}
		return page;
	}
}
