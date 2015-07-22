AJS.bind("init.rte", function()
{ 
	var macroName = 'redmine-macro';

	var dialog = new AJS.Dialog(800, 600);

	var panel = Nenl.Templates.Redmine.search();

	dialog.addHeader("Redmine macro");

	dialog.addPanel("Search panel", panel, "panel-body");

	dialog.addSubmit("Create Macro", function()
			{   
		var checkboxes = AJS.$("input[name=redmineCheckbox]");

		var ids = [];
		for(var i = 0; i < checkboxes.length; i++)
		{
			if(checkboxes[i].checked)
			{
				ids.push(checkboxes[i].id)
			}
		}

		var fieldsIterator = AJS.$("option[name=redmineOption]");

		var fields = [];
		for(var i = 0; i < fieldsIterator.length; i++)
		{
			if(fieldsIterator[i].selected)
			{
				fields.push(fieldsIterator[i].innerHTML);
			}
		}

		var parameters = {ids : ids, fields : fields};

		tinymce.confluence.macrobrowser.macroBrowserComplete({name: "redmine-macro", bodyHtml: undefined, params: {"Parameters" : JSON.stringify(parameters)}});

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
				url : AJS.Confluence.getBaseUrl() + "/plugins/servlet/redmine/getHost",
				success : function(someData1, someData2, response)
				{
					var url = response.getResponseHeader("redmineHost") + "/issues.json?" + document.getElementById("searchQuery").value;

					AJS.$.ajax({
						url : url,
						success : function(data)
						{
							document.getElementById("table").innerHTML = Nenl.Templates.Redmine.table({issues : data.issues.reverse()});
						},
						dataType : "jsonp"});
				}});
		};

		AJS.$("#searchForm").on("submit", function(e)
				{
			e.preventDefault();
			searchRedmine();
				});

		AJS.$("#select2-redmine").auiSelect2();

		dialog.show();
		}
	});
});