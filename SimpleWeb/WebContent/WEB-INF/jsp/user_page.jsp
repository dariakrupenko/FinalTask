<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/SimpleWeb/css/simpleweb.css"/>
<title>Simple Web Application : User Page</title>
<fmt:setLocale value="${sessionScope.locale}"  />
<fmt:setBundle basename="localization.locale" var="locale" />
</head>
<body>
	<header></header>
	<section>
		<h1><fmt:message bundle='${locale}' key='local.title.user_page' /></h1>
		<p>
			<fmt:message bundle='${locale}' key='local.greeting' />,
			${requestScope.user.name}!
		</p>
		<p><fmt:message bundle='${locale}' key='local.redirect' /></p>
	</section>
</body>
</html>