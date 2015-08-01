
jQuery(function() {
    jQuery( document ).tooltip();
});


jQuery(document).ready(function(){


    /*********************************************************************************''
     *
     *      rephrase is changing the full text fragment.
     *
     *      If there is an input field we submit it. Otherwise we just bring up the
     *      input field and fill it with the original or new text.
     *
     *
     */


    jQuery(".toggleRephrase").click(function(){

        textField = jQuery(this).parent().parent().find(".textcol");                // Lookup the text field
        id = jQuery(this).parent().parent().parent().find("div.rowNo").text();      // Lookup the id for submission

        if(textField.find(".input").length > 0){

            // There is an input field. This means we should submit and put the text back.

            newText= textField.find(".replace").val();         // Get the new text from the input field
            //alert("Rephrase found input " + newText);

            // Set the new text and strike-through the old text
            textField.find(".newtext").text(newText);
            textField.find(".text").addClass("deprecated");

            // Replace the input class

            formField = textField.find(".input");
            formField.addClass("noinput");
            formField.removeClass("input");

            //Change the button back to the original symbol
            jQuery(this).html("<img src=\"/bo/adminCommon/styles/bootstrap/images/icons/default/chat.png\">");

            // Submit here

        }
        else{

            // No input field.

            //Change the class to input. This will make it visible
            formField = textField.find(".noinput")
            formField.addClass("input");
            formField.removeClass("noinput");


            // Check if there is a new text. If so we use that
            // and otherwise we use the original text

            if(textField.find(".newtext").text().length > 0){

                formField.find(".replace").val(textField.find(".newtext").text());
                textField.find(".newtext").text("");
            }
            else{
                formField.find(".replace").val(textField.find(".text").text());

            }

            // Change the button to a save symbol.

            jQuery(this).html("<img src=\"/bo/adminCommon/styles/bootstrap/images/icons/default/save.png\">");

        }


    });


    /********************************************************
     *
     *          Click on the toggle field to switch between text
     *          and an input field
     *
     */

  jQuery(".toggleAnnotation").click(function(){

      annotationField = jQuery(this).parent().parent().find(".annotation");         //Lookup input field
      id = jQuery(this).parent().parent().find("div.rowNo").text();      // Lookup the id
      if(annotationField.find("#edit").length >0){

        // There is an input field. Action: Submit
          content = annotationField.find("#edit").val();

          //alert("Submitting:" + content + " id:" + id);

          //change this to API web service to store the new Annotation

          jQuery.post("demo_test_post.asp", { name: content, id: id },
                function(data,status){
                    alert("Data: " + data + "\nStatus: " + status);
                });

          // Change the <a> back to the pen icon and reset the content field to text (from the input field)

          jQuery(this).html("<img src=\"/bo/adminCommon/styles/bootstrap/images/icons/default/editor.png\">");
          jQuery(annotationField).html(content);

      }
      else{

          // No input field. Add input field with the existing text to allow editing

          content = annotationField.text();
          //alert("Found text: " + content);

          //Add the input field and change the <a> to a save icon

          annotationField.html("<input type=text id=\"edit\" value=\""+content+"\"></input>");
          jQuery(this).html("<img src=\"/bo/adminCommon/styles/bootstrap/images/icons/default/save.png\">");
      }
  });

    jQuery("#submit_form").click( function(){

        submitSearch();
    });


    jQuery.fn.enterKey = function (fnc) {
        return this.each(function () {
            jQuery(this).keypress(function (ev) {
                var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                if (keycode == '13') {
                    fnc.call(this, ev);
                }
            })
        })
    };


    jQuery("#InputField").enterKey(function () {

        submitSearch();

    });



    /*

    jQuery("#submit_form").click(function() {

    //alert("clicked");

    var testData = [ 1, 3];
    var searchString = jQuery("#InputField").val();
    var url = "../customPages/services/filterFragments.jsp?text=" + searchString;

    jQuery.get(url,function(replyData,status){

        //alert("Data: " + data + "\nStatus: " + status);
        selectFragments(replyData);
        selectHeadlines(replyData);

        removeHighlight();
        setHighlight(searchString);
     });


    return false; // avoid to execute the actual submit of the form.

    });

      */

    jQuery(".classificationBox").click(function(){

       var classes = ["none", "risk", "expensive", "blocker"];
       var tooltips = ["click to add risk classification",
                   "elevated risk but probably managable. Investigate",
                   "Probably very expensive to comply to.",
                   "A total blocker or deal breaker"];

        var id = jQuery(this).parent().find("div.rowNo").text();      // Lookup the id


       var currentClass = getClass(this, classes);
       var nextClass = getNext(currentClass, classes);
        jQuery(this).removeClass(classes[currentClass]);
        jQuery(this).addClass(classes[nextClass]);
       setTooltip(this, nextClass, tooltips);

       submitTooltip(nextClass, id);

    });

    jQuery(".reference").click(function(){

        alert("Reference clicked. Not implemented yet.");

    });

});

/**************************************************************************'
 *
 *          Submitting a search posts the form value to the server and receives a reply.
 *
 *          TThis is used to select fragments, headlines and set the correct highlights.
 *
 *          For the format of the reply please look in the file filterFRAGMENTS.JSP
 *
 *
 * @return {Boolean}
 */


function submitSearch(){

    alert("submitting!");

    var testData = [ 1, 3];
    var searchString = jQuery("#InputField").val();
    var url = "../customPages/services/filterFragments.jsp?text=" + searchString;

    jQuery.get(url,function(replyData,status){

        //alert("Data: " + data + "\nStatus: " + status);
        selectFragments(replyData);
        selectHeadlines(replyData);

        removeHighlight();
        setHighlight(searchString);
     });


    return false; // avoid to execute the actual submit of the form.

}

function submitTooltip(current, id){

    //alert("Submitting:" + current + " id:" + id);

    /*
    jQuery.post("demo_test_post.asp", { name: content, id: id },
          function(data,status){
              alert("Data: " + data + "\nStatus: " + status);
          });
     */
}

function setTooltip(current, newClass, tooltips){

   jQuery(current).tooltip({ content: tooltips[newClass] });
}


/****************************************************************************************
 *
 *
 *          Setting highlights on the correct words by looking up the span class "regularWord" and
 *          comparing to all the words in the string passed in.
 *
 * @param searchResult
 */

function setHighlight(searchResult){

    strings = searchResult.split(" ");

    jQuery(".regularWord").each(function(){

        textWord = jQuery(this).text()

        for(i = 0; i < strings.length; i++){

            //alert("searchWord: " + strings[i]);

            if(contains(textWord.toLowerCase(), strings[i].toLowerCase())){

                //alert("adding highlight for word: " + jQuery(this).text() )
                jQuery(this).addClass("highlight");
            }

        }

    })

}

function removeHighlight(){

    jQuery(".highlight").each(function(){

        jQuery(this).removeClass("highlight");
    })

}



function getClass(element, classes){

   var currentClass = element.className;
   for(var i = 0; i < classes.length; i++) {

      //alert("comparing '" + currentClass + "' with '" + classes[i] + "'");

      if(contains(currentClass, classes[i]))
          return i;

   }
   return 0;  // Default
}

function contains(string, pattern) {
    return string.indexOf(pattern) != -1;

}

function getNext(current, classes){

   return (current + 1) % classes.length;
}

function selectHeadlines(showValues){

    //Get the headlines part of the json
    var obj = eval ("(" + showValues + ")");
    var rowArray = obj.headline;


    // Not implemented. Should be similar to the selectFragments
}


/*****************************************************
 *
 *
 *
 * @param showValues
 */

function selectFragments(showValues){

    var obj = eval ("(" + showValues + ")");
    var rowArray = obj.fragments;

    //alert("in select: " + rowArray + " length: " + rowArray.length);
    var lastLine = -1;
    var line;
    for(var i = 0; i < rowArray.length; i++) {

       if(i >0)
          lastLine = line;
       line = rowArray[i].show;

       // Hide all in between

       for(var j = lastLine+1; j < line; j++) {

           jQuery(".fragmentRow").eq(j).slideUp("slow");

       }

       jQuery(".fragmentRow").eq(line).slideDown("slow");

        if(i == rowArray.length -1 ){

            // Hide all remaining
            jQuery(".fragmentRow").eq(line).nextAll().slideUp("slow");

        }

    }
}



