<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/SimpleWeb/css/simpleweb.css"/>
<title>Simple Web Application : Error Page</title>
<fmt:setLocale value="${sessionScope.locale}"  />
<fmt:setBundle basename="localization.locale" var="locale" />
</head>
<body>
	<header></header>
	<section>
		<p><strong><fmt:message bundle='${locale}' key='local.error' /></strong></p>
		<p>
		${requestScope.errorMessage}
		</p>
		<p><fmt:message bundle='${locale}' key='local.redirect' /></p>
	</section>
</body>
</html>