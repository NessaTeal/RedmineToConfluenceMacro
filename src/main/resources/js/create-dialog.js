AJS.bind("init.rte", function() { 
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

    var query = AJS.$("#searchQuery").val();

    var parameters = {"ids" : ids, "fields" : fields, "query" : query};

    tinymce.confluence.macrobrowser.macroBrowserComplete({name: "redmine-macro", bodyHtml: undefined, params: {"Parameters" : JSON.stringify(parameters)}});

    dialog.hide();	
          });

  dialog.addCancel("Cancel", function()
          {
    dialog.hide();
          });

  AJS.MacroBrowser.setMacroJsOverride('redmine-macro', {opener: function(macro)
    {
    var restoreDialog = function()
    {
      var parameters = JSON.parse(AJS.$("iframe").contents()
              .find("img[data-macro-name=redmine-macro")
              .attr("data-macro-parameters").replace('Parameters=','').replace('\\',''));

      var ids = parameters.ids;

      var fields = parameters.fields;

      var checkboxes = AJS.$("input[name=redmineCheckbox]");

      for(var i = 0; i < checkboxes.length; i++)
      {
        if(AJS.$.inArray(checkboxes[i].id,ids) == -1)
        {
          checkboxes[i].checked = false;
        }
      }

      AJS.$("option[name=redmineOption]").each(function()
              {
        if(fields.indexOf($(this).text()) > -1)
        {
          $(this).attr("selected", true);
        }
              })
    }

    var searchRedmine = function(restoreSearch)
    {			
      AJS.$.ajax({
        url : AJS.Confluence.getBaseUrl() + "/plugins/servlet/redmine/getHost",
        success : function(someData1, someData2, response)
        {
          if(restoreSearch)
          {
            AJS.$("#searchQuery").val(JSON.parse(AJS.$("iframe").contents()
                    .find("img[data-macro-name=redmine-macro")
                    .attr("data-macro-parameters").replace('Parameters=','').replace('\\','')).query);
          }
          var url = response.getResponseHeader("redmineHost") + "/issues.json?" + document.getElementById("searchQuery").value;

          AJS.$.ajax({
            url : url,
            success : function(data)
            {
              document.getElementById("table").innerHTML = Nenl.Templates.Redmine.table({issues : data.issues.reverse()});

              if(restoreSearch)
              {
                restoreDialog();

                AJS.$("#select2-redmine").auiSelect2();

                dialog.show();
              }
            },
            dataType : "jsonp"});
        }});
    };

    AJS.$("#searchForm").on("submit", function(e) {
              e.preventDefault();
              searchRedmine(false);
            });
	    if(AJS.$("iframe").contents()
	            .find("img[data-macro-name=redmine-macro")
	            .attr("data-macro-parameters") == undefined) {
	      AJS.$("#select2-redmine").auiSelect2();
	
	      dialog.show();
	    }
	    else
	    {
	      searchRedmine(true);
	    }
    }
  });
});