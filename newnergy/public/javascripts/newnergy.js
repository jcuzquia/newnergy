function deleteMeter(delUrl) {
	$.ajax({
		url: delUrl,
		type: 'DELETE',
		success: function(results){
			location.reload();
		},
		error: function() {
			alert("Error deleting")
		}
	});
}

/**
 * Ajax call that handles the deletion of the project
 * @param delUrl
 */
function deleteProject(delUrl){
	$.ajax({
		url: delUrl,
		type: 'DELETE',
		success: function(results){
			location.reload();
		},
		error: function() {
			alert("Error deleting")
		}
	});
}


function showHeatMap(meterUrl, id) {
	var as = jsProjectRoutes.controllers.ProjectController.findMeter(id);
	$.ajax({
        url: as.url,
        dataType: 'json',
        success: function(data) {
        	window.location = meterUrl;
        },
        error: function() {
          alert("there was an error");
        }
      });
	
	
	
}

