AJS.toInit(function() {
	function updateConfig() {
		AJS.$.ajax({
			type : "POST",
			data : {
				redmineHost : AJS.$("#redmineHost").val(),
				apiKey : AJS.$('#apiKey').val()
			}
		});
	}

	AJS.$("#redmineAdmin").on("submit", function(e) {
		e.preventDefault();
		updateConfig();
	});
});