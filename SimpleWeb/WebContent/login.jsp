<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/simpleweb.css"/>
<title>Simple Web Application : Log In</title>
<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="localization.locale" var="locale" />
</head>
<body>
	<header></header>
	<section>
		<h1>
			<fmt:message bundle='${locale}' key='local.title.login' />
		</h1>
		<form action="Controller" method="post">
			<input type="hidden" name="command" value="login" />
				<div class="form-table">
					<div>
						<div></div>
						<div>
							<c:if test="${requestScope.loginFailed}">
								<span class="error-message"> <fmt:message
										bundle='${locale}' key='local.error.login_failed' />
								</span>
							</c:if>
						</div>
					</div>
					<div>
						<label> <fmt:message bundle='${locale}'
								key='local.form.login' />:
						</label> <input type="text" name="login" value="" size="30" />
					</div>

					<div>
						<label> <fmt:message bundle='${locale}'
								key='local.form.password' />:
						</label> <input type="password" name="password" value="" size="30" />
					</div>


				</div>
				<div>
					<input class="button" type="submit"
						value="<fmt:message bundle='${locale}' key='local.label.login' />" />
				</div>
		</form>
		<p><fmt:message bundle='${locale}' key='local.redirect' /></p>
	</section>
</body>
</html>