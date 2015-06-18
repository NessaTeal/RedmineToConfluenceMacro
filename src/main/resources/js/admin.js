AJS.toInit(function()
{
	function load()
	{
		AJS.$.ajax({
			url : AJS.Confluence.getBaseUrl() + "/plugins/servlet/redmine/getHost",
			success : function(a, b, response)
			{
				AJS.$("#redmineHost").val(response.getResponseHeader("redmineHost"));
			}
		});
	}
	
	function updateConfig()
	{
		AJS.$.ajax({
			  type : "POST",
			  data : {redmineHost : AJS.$("#redmineHost").val()}});
	}
	
	AJS.$("#redmineAdmin").on("submit", function(e)
	{
		e.preventDefault();
		updateConfig();
	});
});