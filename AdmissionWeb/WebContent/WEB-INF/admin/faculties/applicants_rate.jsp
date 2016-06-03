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
<title><fmt:message key="i18n.head.faculties_managing" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../../jspf/header.jspf"%>
  <main>
  <section class="applicants-rate-panel">
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit"
        value="<fmt:message key="i18n.button.return_to_admin" bundle="${i18n}" />"
        class="button admin" />
    </form>
    <div>
      <img src="img/icon/faculties_icon.png" />
      <h1>
        <fmt:message key="i18n.title.faculties_managing" bundle="${i18n}" />
      </h1>
    </div>
    <ul>
      <li><form action="Controller">
          <input type="hidden" name="command" value="get-faculty" />
          <input type="hidden" name="for-admin" value="true" />
          <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
          <input type="submit"
            value="<fmt:message key="i18n.button.edit_faculty" bundle="${i18n}" />" />
        </form></li>
      <c:choose>
        <c:when test="${requestScope.admitted}">
          <li class="selected"><fmt:message key="i18n.button.admitted_applicants"
              bundle="${i18n}" /></li>
          <li><form action="Controller"  method="post">
              <input type="hidden" name="command" value="get-register-by-status" />
              <input type="hidden" name="admitted" value="false" />
              <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
              <input type="submit"
                value="<fmt:message key="i18n.button.not_admitted_applicants" bundle="${i18n}" />" />
            </form></li>
          <c:set var="title"
            value="<fmt:message key='i18n.button.admitted_applicants' bundle='${i18n}' />" />
        </c:when>
        <c:otherwise>
          <li><form action="Controller"  method="post">
              <input type="hidden" name="command" value="get-register-by-status" />
              <input type="hidden" name="admitted" value="true" />
              <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
              <input type="submit"
                value="<fmt:message key="i18n.button.admitted_applicants" bundle="${i18n}" />" />
            </form></li>
          <li class="selected"><fmt:message key="i18n.button.not_admitted_applicants"
              bundle="${i18n}" /></li>
          <c:set var="title"
            value="<fmt:message key='i18n.button.not_admitted_applicants' bundle='${i18n}' />" />
        </c:otherwise>
      </c:choose>
    </ul>
    <h2>${requestScope.faculty.title}</h2>
    <h2>${title}</h2>
    <c:choose>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key='i18n.error.admin.faculties.applicants_rate' bundle='${i18n}' />
        </p>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <p class="info-message">
          <fmt:message key='i18n.info.admin.faculties.applicants_rate_empty' bundle='${i18n}' />
        </p>
      </c:when>
      <c:otherwise>
        <div class="control-panel clearfix">
          <form action="Controller"  method="post">
            <input type="hidden" name="command" value="get-register-by-status" />
            <input type="hidden" name="admitted" value="${requestScope.admitted}" />
            <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller"  method="post">
            <input type="hidden" name="command" value="get-register-by-status" />
            <input type="hidden" name="admitted" value="${requestScope.admitted}" />
            <input type="hidden" name="faculty-id" value="${requestScope.faculty.id}" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="true" />
            <input type="submit" value="<fmt:message key="i18n.button.next" bundle="${i18n}" />"
              class="button" />
          </form>
        </div>
        <table>
          <thead>
            <tr>
              <td><fmt:message key="i18n.label.name" bundle="${i18n}" /></td>
              <c:forEach var="d" items="${requestScope.faculty.disciplines}">
                <td class="center">${d.title}</td>
              </c:forEach>
              <td class="center"><fmt:message key="i18n.label.certificate" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.total_score" bundle="${i18n}" /></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${requestScope.list}">
              <tr>
                <td><form action="Controller">
                    <input type="hidden" name="command" value="get-applicant" />
                    <input type="hidden" name="applicant-id" value="${r.applicant.id}" />
                    <input type="submit" value="${r.applicant.name}">
                  </form></td>
                <c:forEach var="d" items="${requestScope.faculty.disciplines}">
                  <td class="center">${r.scores[d]}</td>
                </c:forEach>
                <td class="center">${r.certificateScore}</td>
                <td class="center">${r.totalScore}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>