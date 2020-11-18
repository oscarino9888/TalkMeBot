$(document).ready( function () {
	$("#accetta").hide();
});



 $("#testolibero").keyup(function(event) {
    if (event.keyCode === 13) {
        $("#accetta").click();
    }
});


$( "#start" ).click(function() {
	
	

  $("#testolibero").css("visibility","visible");
  $("#bigtitle").hide();
  $("#paragrafo1").hide();
  $("#start").hide();
  $("#accetta").show();
  
  
  
  $.ajax({
		url: `BotServlet`,
		async: true,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	
	    }
	   		
});
  
  
});

$( "#accetta" ).click(function() {
	var val=document.getElementById("testolibero").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#testolibero").val("");

	    	
	    }
	   		
});
	

	

	
});







