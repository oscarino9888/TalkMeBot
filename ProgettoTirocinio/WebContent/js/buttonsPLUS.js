$("#accetta").hide();
$("#testolibero").hide();


$( "#but1" ).click(function() {
	var val=document.getElementById("but1").value
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});

$( "#but2" ).click(function() {
	var val=document.getElementById("but2").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});


$( "#but3" ).click(function() {
	var val=document.getElementById("but3").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});

$( "#but4" ).click(function() {
	var val=document.getElementById("but4").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});



$( "#but5" ).click(function() {
	var val=document.getElementById("but5").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});


$( "#but6" ).click(function() {
	var val=document.getElementById("but6").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    }
	   		
});
	
});


$( "#but7" ).click(function() {
	var val=document.getElementById("but7").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	$("#accetta").show();
	    	$("#testolibero").show();
	    		    }
	   		
});
	
});

$( "#noresponse" ).click(function() {
	var val=document.getElementById("noresponse").value;
	$.ajax({
		url: `BotServlet?risposta=${val}`,
		async: false,
      method: 'get',		
	    dataType:'json',
	    success: function(data){
	    	$("#talkText").html(data);
	    	
	    }
	   		
});
	
	
	
});