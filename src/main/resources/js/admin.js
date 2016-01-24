//Start only when Condluence is fully loaded
AJS.toInit(function() {
	//POST request to update Redmine URL and API key
	function updateConfig() {
		AJS.$.ajax({
			type : "POST",
			data : {
				redmineHost : AJS.$("#redmineHost").val(),
				apiKey : AJS.$('#apiKey').val()
			}
		});
	}

	//Prevent default reaction on submit and call our function
	AJS.$("#redmineAdmin").on("submit", function(e) {
		e.preventDefault();
		updateConfig();
	});
});