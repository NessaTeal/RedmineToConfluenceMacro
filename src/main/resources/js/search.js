AJS.toInit(function()
{
	var searchRedmine = function()
	{
		var url = "http://localhost/redmine/issues.json?" + AJS.$("#searchQuery").value;
		
		AJS.$.ajax({
				type : "GET",
				url : "http://localhost/redmine/issues.json",
				success : function(data)
				{
			        document.getElementById("table").innerHTML = Nenl.Templates.Redmine.table({issues : data.issues});
				},
				dataType : "jsonp"
				})
	}
	
	document.getElementById("searchRedmine").onclick = searchRedmine;
});