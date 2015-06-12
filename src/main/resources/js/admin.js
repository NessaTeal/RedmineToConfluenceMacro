AJS.toInit(function()
{
	function updateConfig()
	{
		AJS.$.ajax({
			  type: "POST",
			  data: {redmineHost : AJS.$("#redmineHost").val()}});
	}
	
	AJS.$("#redmineAdmin").on("submit", function(e)
	{
		e.preventDefault();
		updateConfig();
	});
});