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
<title><fmt:message key="i18n.head.enrollment_managing" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../../jspf/header.jspf"%>
  <main>
  <section>
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit"
        value="<fmt:message key="i18n.button.return_to_admin" bundle="${i18n}" />"
        class="button admin" />
    </form>
    <div>
      <img src="img/icon/enrollment_icon.png" />
      <h1>
        <fmt:message key="i18n.title.enrollment_managing" bundle="${i18n}" />
      </h1>
    </div>
    <ul>
      <li>
        <form action="Controller"  method="post">
          <input type="hidden" name="command" value="get-enrolls-list" />
          <input type="submit" value="<fmt:message key="i18n.button.enrolls" bundle="${i18n}" />" />
        </form>
      </li>
      <li class="selected"><fmt:message key="i18n.button.open_enroll" bundle="${i18n}" /></li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.start_enroll" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.enrollment.start" bundle="${i18n}" />
    </p>
    <div>
      <c:if test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.enrollment.open" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.validationFailed}">
        <p class="error-message">
          <fmt:message key="i18n.error.validate" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.isCurrentEnroll}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.enrollment.start_is_active" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.successStarted}">
        <p class="success-message">
          <fmt:message key="i18n.success.admin.enrollment.open" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller" class="form-box"  method="post">
      <input type="hidden" name="command" value="start-enroll" />
      <input type="hidden" name="key" value="${applicationScope.generator.generatedKey}" />
      <div>
        <label>
          <fmt:message key="i18n.label.begin_date" bundle="${i18n}" />
          :
        </label>
        <input type="date" name="begin-date"
          value='<fmt:formatDate value="${requestScope.beginDate}" pattern="yyyy-MM-dd"/>' readonly />
      </div>
      <div>
        <div></div>
        <div class="validate-message">
          <fmt:message key="i18n.validate.date_format" bundle="${i18n}" />
        </div>
      </div>
      <div>
        <label>
          <fmt:message key="i18n.label.end_date" bundle="${i18n}" />
          :
        </label>
        <input type="date" name="end-date"
          value='<fmt:formatDate value="${requestScope.enroll.endDate}" pattern="yyyy-MM-dd"/>'
          required />
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.open_enroll" bundle="${i18n}" />"
        class="button wide-button" />
    </form>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>