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
<title><fmt:message key="i18n.head.main" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="WEB-INF/jspf/header.jspf"%>
  <main>
  <section class="main-panel">
    <div>
      <img src="img/icon/account_icon.png" alt="account_icon" />
      <form action="Controller">
        <input type="hidden" name="command" value="enter-account">
        <input type="submit"
          value="<fmt:message key="i18n.head.applicant_login" bundle="${i18n}" />" class="title" />
      </form>
      <c:choose>
        <c:when test="${!empty sessionScope.applicant}">
          <p>
            <fmt:message key="i18n.info.applicant.login.not_empty.hello" bundle="${i18n}" />
            , ${sessionScope.applicant.name}.
            <fmt:message key="i18n.info.applicant.login.not_empty" bundle="${i18n}" />
          </p>
        </c:when>
        <c:otherwise>
          <p>
            <fmt:message key="i18n.info.applicant.login.empty" bundle="${i18n}" />
          </p>
        </c:otherwise>
      </c:choose>
    </div>
    <div>
      <img src="img/icon/registrate_icon.png" alt="reg_icon" /> <a
        href="applicant_registration.jsp" class="title link-button"><fmt:message
          key="i18n.head.applicant_registration" bundle="${i18n}" /></a>
      <p>
        <fmt:message key="i18n.info.applicant.registration" bundle="${i18n}" />
      </p>
    </div>
    <div>
      <img src="img/icon/faculties_list_icon.png" alt="fac_list_icon" />
      <form action="Controller" method="post"  method="post">
        <input type="hidden" name="command" value="get-faculties-list">
        <input type="hidden" name="for-admin" value="false">
        <input type="submit" value="<fmt:message key="i18n.head.faculties_list" bundle="${i18n}" />"
          class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.faculties_list" bundle="${i18n}" />
      </p>
    </div>
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit" value="<fmt:message key="i18n.button.admin_login" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="WEB-INF/jspf/footer.jspf"%>
</body>
</html>