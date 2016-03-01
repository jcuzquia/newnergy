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


function showHeatMap(meterUrl, meterId) {
	var meter="#meter-button-";
	var id=meterId;
	var buttonId= meter.concat(id);
	$('a[href="#heatmap"]').trigger('click');
}

