<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,Beans.*,Database.*,Servlet.*"%>
    
    <%
	/*Admin admin = (Admin) session.getAttribute("admin");

	if (admin != null) {
	

	} else {
		response.sendRedirect("adminLogin.jsp");
	}
 */
 
    int id=Integer.parseInt((String)session.getAttribute("id"));
 
	ArrayList<Domanda> domande = new ArrayList<>();

	int count=0;
	int countpositive=0;
	int countnegative=0;
	int countneutral=0;
	int row=0;
	domande = DatabaseQuery.getDomandeExtra(id);

%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>TalkMeBot Domande extra </title>

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- <link rel="manifest" href="site.webmanifest"> -->
    <link rel="shortcut icon" type="image/x-icon" href="img/favicon.jpg">
    <!-- Place favicon.ico in the root directory -->

    <!-- CSS here -->
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/owl.carousel.min.css">
    <link rel="stylesheet" href="css/magnific-popup.css">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/themify-icons.css">
    <link rel="stylesheet" href="css/nice-select.css">
    <link rel="stylesheet" href="css/flaticon.css">
    <link rel="stylesheet" href="css/gijgo.css">
    <link rel="stylesheet" href="css/animate.css">
    <link rel="stylesheet" href="css/slicknav.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/BotpageExtraStyle.css">
</head>
<body>
<%
for (int i = 0; i < domande.size(); i++) {
	if(domande.get(i).getValore().equalsIgnoreCase("Non Voglio Rispondere"))
		count++;
	if(domande.get(i).getGrado().equalsIgnoreCase("NEGATIVE")){
		countnegative++;
	}
	if(domande.get(i).getGrado().equalsIgnoreCase("NEUTRAL")){
		countneutral++;
	}
	if(domande.get(i).getGrado().equalsIgnoreCase("POSITIVE")){
		countpositive++;
	}
		
}
						%>



<jsp:include page="header.jsp" />


     <div class="testarea">   
     
     
     <table class="table table-hover">
					<thead class="th-center">
						<tr>
						
							<th>N°Domanda</th>
							<th>Valore</th>
							<th>Grado</th>
							<th>N\A Risposte</th>
							<th>N.Negative</th>
							<th>N.Positive</th>
							<th>N.Neutrali</th>
							
						</tr>
					</thead>
					<tbody>

						<%

								for (int i = 0; i < domande.size(); i++) {

									
						%>
						<form action="" method="get">
						<tr>
							<td><%=domande.get(i).getN_domanda()%></td>
							<td><%=domande.get(i).getValore()%></td>
							<td><%=domande.get(i).getGrado()%></td>
							<%if(row==0){%>
								<td><%=count%></td>
								<td><%=countnegative%></td>
								<td><%=countpositive%></td>
								<td><%=countneutral%></td>
							<% row++;}  %>
						   
						</tr>
						</form>
						
						<%
							}
						%>
					</tbody>
				</table>
				
				
				                 
<p id="talkText"> </p>






<input type="text" id="testolibero">

</div>




 <script src="js/vendor/modernizr-3.5.0.min.js"></script>
    <script src="js/vendor/jquery-1.12.4.min.js"></script>
    <script src="js/popper.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/owl.carousel.min.js"></script>
    <script src="js/isotope.pkgd.min.js"></script>
    <script src="js/ajax-form.js"></script>
    <script src="js/waypoints.min.js"></script>
    <script src="js/jquery.counterup.min.js"></script>
    <script src="js/imagesloaded.pkgd.min.js"></script>
    <script src="js/scrollIt.js"></script>
    <script src="js/jquery.scrollUp.min.js"></script>
    <script src="js/wow.min.js"></script>
    <script src="js/nice-select.min.js"></script>
    <script src="js/jquery.slicknav.min.js"></script>
    <script src="js/jquery.magnific-popup.min.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/gijgo.min.js"></script>
    <script src="js/boscripts/botstart.js"></script>

    <!--contact js-->
    <script src="js/contact.js"></script>
    <script src="js/jquery.ajaxchimp.min.js"></script>
    <script src="js/jquery.form.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/mail-script.js"></script>

    <script src="js/main.js"></script>

</body>






</html>