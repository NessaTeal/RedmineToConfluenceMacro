AJS.toInit(function()
{ 
    var macroName = 'redmine-macro';

    var dialog = new AJS.Dialog(800, 600);
    
    var panel = Nenl.Templates.Redmine.search();
    
    dialog.addHeader("Redmine macro");
    
    dialog.addPanel("Search panel", panel, "panel-body");
    
	dialog.addSubmit("Create Macro", function()
    {
    	var selection = AJS.Rte.getEditor().selection.getNode();
        var macro = {
            name: macroName
        };
        
        tinymce.plugins.Autoconvert.convertMacroToDom(macro, function(data, textStatus, jqXHR)
        {
            AJS.$(selection).html(data + "<p><br/></p>");
        },
        function(jqXHR, textStatus, errorThrown)
        {
            AJS.log("error converting macro to DOM");
        });
        
        dialog.hide();
    });

    dialog.addCancel("Cancel", function()
    {
        dialog.hide();
    });

    AJS.MacroBrowser.setMacroJsOverride(macroName, {opener: function(macro)
    {
    	var searchRedmine = function()
		{			
			AJS.$.ajax({
				url : AJS.Confluence.getBaseUrl() + "/plugins/servlet/redmine/admin",
				data : {getHost : ""},
				success : function(someData1, someData2, response)
				{
					var url = response.getResponseHeader("redmineHost") + "/issues.json?" + document.getElementById("searchQuery").value;
	
					AJS.$.ajax({
						url : url,
						success : function(data)
						{
							document.getElementById("table").innerHTML = Nenl.Templates.Redmine.table({issues : data.issues});
					    },
						dataType : "jsonp"});
				}});
		}
    	
    	AJS.$("#searchForm").on("submit", function(e)
    	{
    		e.preventDefault();
    		searchRedmine();
    	});
    	
		dialog.show();
    }});
});