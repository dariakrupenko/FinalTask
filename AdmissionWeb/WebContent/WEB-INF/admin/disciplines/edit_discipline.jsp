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

    <h2>
      <fmt:message key="i18n.title.edit_discipline" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.disciplines.edit" bundle="${i18n}" />
    </p>
    <c:choose>
      <c:when test="${requestScope.successDeleted}">
        <p class="success-message">
          <fmt:message key="i18n.success.admin.disciplines.delete" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${empty requestScope.discipline}">
        <p class="info-message">
          <fmt:message key="i18n.error.admin.disciplines.not_found" bundle="${i18n}" />
        </p>
      </c:when>

      <c:otherwise>
        <c:if test="${requestScope.error}">
          <p class="error-message">
            <fmt:message key="i18n.error.admin.disciplines.delete" bundle="${i18n}" />
          </p>
        </c:if>
        <c:if test="${requestScope.deleteUnable}">
          <p class="error-message">
            <fmt:message key="i18n.error.admin.disciplines.delete_current_enroll" bundle="${i18n}" />
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
        <c:if test="${requestScope.successUpdated}">
          <p class="success-message">
            <fmt:message key="i18n.success.admin.disciplines.update" bundle="${i18n}" />
          </p>
        </c:if>
        <form action="Controller" class="form-box" method="post">
          <input type="hidden" name="command" value="update-discipline" />
          <input type="hidden" name="discipline-id" value="${requestScope.discipline.id}"
            maxLength="30" required />
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
            <input type="text" name="title" value="${requestScope.discipline.title}" required />
          </div>
          <input type="submit" value="<fmt:message key="i18n.button.edit" bundle="${i18n}" />"
            class="button" />
        </form>
        <form>
          <input type="hidden" name="command" value="delete-discipline" />
          <input type="hidden" name="discipline-id" value="${requestScope.discipline.id}" />
          <input type="submit" value="<fmt:message key="i18n.button.delete" bundle="${i18n}" />"
            class="button" />
        </form>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>