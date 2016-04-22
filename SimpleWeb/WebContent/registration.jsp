<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/simpleweb.css"/>
<title>Simple Web Application : Registration</title>
<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="localization.locale" var="locale" />
</head>
<body>
	<header></header>
	<section>
		<h1>
			<fmt:message bundle='${locale}' key='local.title.reg' />
		</h1>
		<h3>
			<fmt:message bundle='${locale}' key='local.title.fields_req' />
		</h3>
		<form action="Controller" method="get">
			<input type="hidden" name="command" value="registration" />
				<div class="form-table">
					<div>
						<label> <fmt:message bundle='${locale}'
								key='local.form.login' />:
						</label> <input type="text" name="login" value="${requestScope.login}" size="30" />
					</div>
					<div>
						<div></div>
						<c:if test="${requestScope.loginError}">
							<span class="error-message"> <fmt:message
									bundle='${locale}' key='local.error.login' />
							</span>
						</c:if>
					</div>
					<div>
						<label> <fmt:message bundle='${locale}'
								key='local.form.password' />:
						</label> <input type="password" name="password" value="" size="30" />
					</div>
					<div>
						<div></div>
						<c:if test="${requestScope.passwordError}">
							<span class="error-message"> <fmt:message
									bundle='${locale}' key='local.error.password' />
							</span>
						</c:if>
					</div>
					<div>
						<label> <fmt:message bundle='${locale}'
								key='local.form.name' />:
						</label> <input type="text" name="name" value="${requestScope.name}" size="30" />
					</div>
					<div>
						<div></div>
						<c:if test="${requestScope.nameError}">
							<span class="error-message"> <fmt:message
									bundle='${locale}' key='local.error.name' />
							</span>
						</c:if>
					</div>
				</div>
				<div>
					<input class="button" type="submit"
						value="<fmt:message bundle='${locale}' key='local.label.reg' />" />
				</div>
		</form>
		<p><fmt:message bundle='${locale}' key='local.redirect' /></p>
	</section>
</body>
</html>