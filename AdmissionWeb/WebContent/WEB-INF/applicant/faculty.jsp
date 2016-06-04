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
<title><fmt:message key="i18n.head.faculties_list" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../jspf/header.jspf"%>
  <main>
  <section class="faculty-panel">
    <c:choose>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.faculty" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.notFound}">
        <p class="info-message">
          <fmt:message key="i18n.error.faculty.not_found" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <div>
          <img src="img/logo/${requestScope.faculty.logoname}" />
          <h1>${requestScope.faculty.title}</h1>
        </div>
        <form action="Controller">
          <input type="hidden" name="command" value="to-registry-for-faculty-page" />
          <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
          <dl>
            <dt>
              <fmt:message key="i18n.label.description" bundle="${i18n}" />
            </dt>
            <dd>${requestScope.faculty.description}</dd>
            <dt>
              <fmt:message key="i18n.title.contacts_inf" bundle="${i18n}" />
            </dt>
            <dd>
              <fmt:message key="i18n.label.dean_name" bundle="${i18n}" />
              : <em>${requestScope.faculty.dean}</em><br />
              <fmt:message key="i18n.label.address" bundle="${i18n}" />
              : <em>${requestScope.faculty.address}</em><br />
              <fmt:message key="i18n.label.phone" bundle="${i18n}" />
              : <em>${requestScope.faculty.phone}</em>
            </dd>
            <dt>
              <fmt:message key="i18n.title.disciplines_plan" bundle="${i18n}" />
            </dt>
            <dd>
              <fmt:message key="i18n.label.disciplines" bundle="${i18n}" />
              :
              <c:forEach var="d" items="${requestScope.faculty.disciplines}">
          ${d.title} |</c:forEach>
              <br />
              <fmt:message key="i18n.label.plan" bundle="${i18n}" />
              : ${requestScope.faculty.plan}
            </dd>
            <dt>
              <fmt:message key="i18n.label.pass_rate" bundle="${i18n}" />
            </dt>
            <dd>
              <strong>${requestScope.faculty.passRate}</strong>
            </dd>
          </dl>
          <input class="button" type="submit"
            value="<fmt:message key="i18n.button.registry" bundle="${i18n}" />" />
        </form>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../jspf/footer.jspf"%>
</body>
</html>