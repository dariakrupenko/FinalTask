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
<title><fmt:message key="i18n.head.admin_console" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../jspf/header.jspf"%>
  <main>
  <section class="main-panel">
    <div>
      <img src="img/icon/register_icon.png" alt="account icon" />
      <form action="Controller" method="post">
        <input type="hidden" name="command" value="get-register">
        <input type="submit"
          value="<fmt:message key="i18n.head.register_review" bundle="${i18n}" />" class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.admin.register.review" bundle="${i18n}" />
      </p>
    </div>
    <div>
      <img src="img/icon/enrollment_icon.png" alt="account icon" />
      <form action="Controller">
        <input type="hidden" name="command" value="get-enrolls-list">
        <input type="submit"
          value="<fmt:message key="i18n.head.enrollment_managing" bundle="${i18n}" />" class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.admin.enrollment_managing" bundle="${i18n}" />
      </p>
    </div>
    <div>
      <img src="img/icon/faculties_icon.png" alt="fac icon" />
      <form action="Controller"  method="post">
        <input type="hidden" name="command" value="get-faculties-list" />
        <input type="hidden" name="for-admin" value="true" />
        <input type="submit"
          value="<fmt:message key="i18n.head.faculties_managing" bundle="${i18n}" />" class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.admin.faculties_managing" bundle="${i18n}" />
      </p>
    </div>
    <div>
      <img src="img/icon/applicants_icon.png" alt="fac icon" />
      <form action="Controller" method="post">
        <input type="hidden" name="command" value="get-applicants-list" />
        <input type="submit"
          value="<fmt:message key="i18n.head.applicants_managing" bundle="${i18n}" />" class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.admin.applicants_managing" bundle="${i18n}" />
      </p>
    </div>
    <div>
      <img src="img/icon/disciplines_icon.png" alt="fac icon" />
      <form action="Controller"  method="post">
        <input type="hidden" name="command" value="get-disciplines-list" />
        <input type="submit"
          value="<fmt:message key="i18n.head.disciplines_managing" bundle="${i18n}" />"
          class="title" />
      </form>
      <p>
        <fmt:message key="i18n.info.admin.disciplines_managing" bundle="${i18n}" />
      </p>
    </div>
    <form action="Controller">
      <input type="hidden" name="command" value="logout" />
      <input class="button" type="submit"
        value="<fmt:message key="i18n.button.logout" bundle="${i18n}" />" />
    </form>
  </section>
  </main>
  <%@include file="../jspf/footer.jspf"%>
</body>
</html>