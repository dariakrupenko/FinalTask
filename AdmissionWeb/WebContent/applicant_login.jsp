<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="${initParam.i18n}" var="i18n" />

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/admission.css" />
<link rel="shortcut icon" href="img/icon/shortcut.png" />
<title><fmt:message key="i18n.head.applicant_login" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="WEB-INF/jspf/header.jspf"%>
  <main>
  <section>
    <div>
      <img src="img/icon/account_icon.png" />
      <h1>
        <fmt:message key="i18n.title.applicant_login" bundle="${i18n}" />
      </h1>
    </div>
    <div>
      <p>
        <fmt:message key="i18n.info.applicant.text" bundle="${i18n}" />
        <a href="applicant_registration.jsp"> <fmt:message key="i18n.info.applicant.text.reg_page"
            bundle="${i18n}" />
        </a>
      </p>
    </div>
    <div>
      <c:if test="${requestScope.loginFailed}">
        <p class="error-message">
          <fmt:message key="i18n.error.login_failed" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.login" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller" class="form-box"  method="post">
      <input type="hidden" name="command" value="login-applicant" />

      <div>
        <label><fmt:message key="i18n.label.login" bundle="${i18n}" />:</label>
        <input type="text" name="login" />
      </div>
      <div>
        <label><fmt:message key="i18n.label.password" bundle="${i18n}" />:</label>
        <input type="password" name="password" />
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.login" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="WEB-INF/jspf/footer.jspf"%>
</body>
</html>