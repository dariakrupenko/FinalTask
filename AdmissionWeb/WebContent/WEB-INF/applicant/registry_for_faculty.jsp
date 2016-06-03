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
<title><fmt:message key="i18n.head.registry_for_faculty" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../jspf/header.jspf"%>
  <main>
  <section>
    <div>
      <img src="img/icon/registry_icon.png" />
      <h1>
        <fmt:message key="i18n.title.registry_for_faculty" bundle="${i18n}" />
      </h1>
    </div>
    <h2>${requestScope.faculty.title}</h2>
    <c:choose>
      <c:when test="${requestScope.successRegistered}">
        <p class="success-message">
          <fmt:message key="i18n.success.applicant.registry" bundle="${i18n}" />
          <fmt:message key="i18n.success.applicant.registry.account" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registry" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.noCurrentEnroll}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registry.no_current_enroll" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.unregistrated}">
        <p class="error-message">
          <fmt:message key="i18n.error.unregistrated" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.registered}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registry.not_empty_record" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <div>
          <p>
            <fmt:message key="i18n.info.applicant.registry" bundle="${i18n}" />
          </p>
        </div>
        <c:if test="${requestScope.validationFailed}">
          <p class="error-message">
            <fmt:message key="i18n.error.validate" bundle="${i18n}" />
          </p>
        </c:if>
        <form class="form-box" action="Controller">
          <input type="hidden" name="command" value="registry-for-faculty" />
          <input type="hidden" name="key" value="${applicationScope.generator.generatedKey}" />
          <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />

          <c:forEach var="d" items="${requestScope.faculty.disciplines}">
            <div>
              <label>${d.title}:</label>
              <input type="number" name="dInf${d.id}.${d.title}" value="${requestScope.scores[d]}"
                required />
            </div>
          </c:forEach>
          <div>
            <label>
              <fmt:message key="i18n.label.certificate" bundle="${i18n}" />
              :
            </label>
            <input type="number" name="certificate-score" value="${requestScope.certificate}"
              required />
          </div>
          <input type="submit" value="<fmt:message key="i18n.button.registry" bundle="${i18n}" />"
            class="button" />
        </form>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../jspf/footer.jspf"%>
</body>
</html>