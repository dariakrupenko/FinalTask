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
<title><fmt:message key="i18n.head.disciplines_managing" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../../jspf/header.jspf"%>
  <main>
  <section class="disciplines-panel">
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit"
        value="<fmt:message key="i18n.button.return_to_admin" bundle="${i18n}" />"
        class="button admin" />
    </form>
    <div>
      <img src="img/icon/disciplines_icon.png" />
      <h1>
        <fmt:message key="i18n.title.disciplines_managing" bundle="${i18n}" />
      </h1>
    </div>
    <ul>
      <li>
        <form action="Controller" method="post">
          <input type="hidden" name="command" value="get-disciplines-list" />
          <input type="submit"
            value="<fmt:message key="i18n.button.disciplines" bundle="${i18n}" />" />
        </form>
      </li>
      <li class="selected">
        <fmt:message key="i18n.button.add_discipline" bundle="${i18n}" />
      </li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.discipline_addition" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.disciplines.add" bundle="${i18n}" />
    </p>
    <div>
      <c:if test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.disciplines.add" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.validationFailed}">
        <p class="error-message">
          <fmt:message key="i18n.error.validate" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.alreadyExists}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.disciplines.exist" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.successAdded}">
        <p class="success-message">
          <fmt:message key="i18n.success.admin.disciplines.add" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller" class="form-box" method="post">
      <input type="hidden" name="command" value="add-discipline" />
      <input type="hidden" name="key" value="${applicationScope.generator.generatedKey}" />
      <div>
        <div></div>
        <div class="validate-message">
          <fmt:message key="i18n.validate.discipline_title" bundle="${i18n}" />
        </div>
      </div>
      <div>
        <label>
          <fmt:message key="i18n.label.discipline_title" bundle="${i18n}" />
          :
        </label>
        <input type="text" name="title" value="${requestScope.discipline.title}" maxLength="30"
          required />
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.add" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>