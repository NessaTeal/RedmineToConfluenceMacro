//Run code only if Confluence system competely loaded
AJS.bind("init.rte", function() {
  //Create new dialog
  var dialog = new AJS.Dialog(800, 600);

  //Create main panel
  var panel = Nenl.Templates.Redmine.search();

  //Give dialog a name
  dialog.addHeader("Redmine macro");

  //Put main panel into dialog
  dialog.addPanel("Search panel", panel, "panel-body");

  //Configure submit button
  dialog.addSubmit("Create Macro", function()
  {   
    //Read all checkboxes with ids of issues
    var checkboxes = AJS.$("input[name=redmineCheckbox]");

    //Create empty array
    var ids = [];

    //If checkbox is checked then push id into array
    for(var i = 0; i < checkboxes.length; i++)
    {
      if(checkboxes[i].checked)
      {
        ids.push(checkboxes[i].id)
      }
    }

    //Get all fields
    var fields = AJS.$("option[name=redmineOption]");

    //Empty array
    var fields = [];

    //Push into array if field is required
    for(var i = 0; i < fields.length; i++)
    {
      if(fields[i].selected)
      {
        fields.push(fields[i].innerHTML);
      }
    }

    //Save query to load it on later openings
    var query = AJS.$("#searchQuery").val();

    //Create map with ids, fields and query
    var parameters = {"ids" : ids, "fields" : fields, "query" : query};

    //Place macro inside Confluence page
    //Very difficult for undestanding Confluence function
    tinymce.confluence.macrobrowser.macroBrowserComplete({name: "redmine-macro", bodyHtml: undefined, params: {"Parameters" : JSON.stringify(parameters)}});

    //Hide dialog
    dialog.hide();	
  });

  //If cancel button is pressed then just hide dialog
  dialog.addCancel("Cancel", function()
  {
    dialog.hide();
  });

  //Override Confluence default dialog pop-up to our own
  AJS.MacroBrowser.setMacroJsOverride('redmine-macro', {opener: function(macro)
    {
      //Function that searches for issues
      //Also called when macro was already placed and reopened
      var searchRedmine = function(restoreSearch)
      {			
        //First request to servlet to get Redmine URL
        AJS.$.ajax({
          url : AJS.Confluence.getBaseUrl() + "/plugins/servlet/redmine/getHost",

          //Afterwards call for issues
          success : function(someData1, someData2, response)
          {
            //If it is reopening then read query stored in parameters string
            if(restoreSearch)
            {
              AJS.$("#searchQuery").val(JSON.parse(AJS.$("iframe").contents()
                .find("img[data-macro-name=redmine-macro")
                .attr("data-macro-parameters").replace('Parameters=','').replace('\\','')).query);
            }

            //Set URL where Redmine is hosted
            var url = response.getResponseHeader("redmineHost") + "/issues.json?" + document.getElementById("searchQuery").value;

            //Ask for issues
            AJS.$.ajax({
              url : url,

              //Place all found issues into table
              success : function(data)
              {
                //Reverse order as initially it is in descending one
                document.getElementById("table").innerHTML = Nenl.Templates.Redmine.table({issues : data.issues.reverse()});

                //If it restore search then call function that will make dialog window looks like before closing
                if(restoreSearch)
                {
                  restoreDialog();

                  //Makes usual HTML select to be beatiful Atlassian UI select
                  AJS.$("#select2-redmine").auiSelect2();

                  dialog.show();
                }
              },
              dataType : "jsonp"});
          }});
      };

      //Function is called if macro was already placed and this is reopening
      var restoreDialog = function()
      {
        //Parse saved parameters
        var parameters = JSON.parse(AJS.$("iframe").contents()
          .find("img[data-macro-name=redmine-macro")
          .attr("data-macro-parameters").replace('Parameters=','').replace('\\',''));

        var ids = parameters.ids;

        var fields = parameters.fields;

        var checkboxes = AJS.$("input[name=redmineCheckbox]");

        //Uncheck appropriate checkboxes
        for(var i = 0; i < checkboxes.length; i++)
        {
          if(AJS.$.inArray(checkboxes[i].id,ids) == -1)
          {
            checkboxes[i].checked = false;
          }
        }

        //Select appropriate fields
        AJS.$("option[name=redmineOption]").each(function()
        {
          if(fields.indexOf($(this).text()) > -1)
          {
            $(this).attr("selected", true);
          }
        })
      }

      //When seacrh button is pressed supress usual reaction and call our function
      AJS.$("#searchForm").on("submit", function(e) {
        e.preventDefault();
        searchRedmine(false);
      });

      //If it is first time created macro then open empty dialog
      if(AJS.$("iframe").contents()
       .find("img[data-macro-name=redmine-macro")
       .attr("data-macro-parameters") == undefined)
      {
        //Makes usual HTML select to be beatiful Atlassian UI select
        AJS.$("#select2-redmine").auiSelect2();
        dialog.show();
      }
      //Else restore prevoius state
      else
      {
        searchRedmine(true);
      }
    }
  });
});