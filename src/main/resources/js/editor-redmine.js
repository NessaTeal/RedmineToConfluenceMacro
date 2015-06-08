// bind on initialization of editor
AJS.bind("init.rte", function() { 
    var macroName = 'redmine-macro';

    // 1. create dialog to add macro
    var dialog = new AJS.Dialog(800, 500);
    
    var panel = Nenl.Templates.Redmine.search();
    
    dialog.addHeader("Redmine macro");
    
    dialog.addPanel("Panel 1", panel, "panel-body");
    
    // hide dialog
    // 2. add macro to editor
    dialog.addSubmit("Create Macro", function() {

        // 3. get current selection in editor
        var selection = AJS.Rte.getEditor().selection.getNode();
        var macro = {
            name: macroName
        };

        // 4. convert macro and insert in DOM
        tinymce.plugins.Autoconvert.convertMacroToDom(macro, function(data, textStatus, jqXHR ) {
            AJS.$(selection).html(data + "<p><br/></p>");
        }, function(jqXHR, textStatus, errorThrown ) {
            AJS.log("error converting macro to DOM");
        });
        dialog.hide();
    });

    dialog.addCancel("Cancel", function() {
        dialog.hide();
    });

    // 5. bind event to open macro browser
    AJS.MacroBrowser.setMacroJsOverride(macroName, {opener: function(macro) {
        // open custom dialog
        dialog.show();
    }});
});