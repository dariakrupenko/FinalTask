<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/simpleweb.css"/>
<title>Simple Web Application : Index Page</title>
<fmt:setLocale value="${sessionScope.locale}"  />
<fmt:setBundle basename="localization.locale" var="locale" />
</head>
<body>
	<header>
		<form action="Controller" method="post">
			<input type="hidden" name="command" value="change-locale"  />
			<input type="hidden" name="lang" value="en" /> 
			<input class="button" type="submit" value="<fmt:message bundle='${locale}' key='local.lang.en' />" />
		</form>
		<form action="Controller" method="post">
			<input type="hidden" name="command" value="change-locale" />  
			<input type="hidden" name="lang" value="ru" /> 
			<input class="button" type="submit" value="<fmt:message bundle='${locale}' key='local.lang.ru' />" />
		</form>
	</header>
	<section>
		<h1><fmt:message bundle='${locale}' key='local.title' /></h1>
		<div class="info-text">
			<a href="registration.jsp"><fmt:message bundle='${locale}' key='local.label.reg' /></a>
			|
			<a href="login.jsp"><fmt:message bundle='${locale}' key='local.label.login' /></a>
		</div>
	</section>
</body>
</html>