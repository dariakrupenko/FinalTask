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
<title><fmt:message key="i18n.head.applicants_managing" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../../jspf/header.jspf"%>
  <main>
  <section class="applicants-panel">
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit"
        value="<fmt:message key="i18n.button.return_to_admin" bundle="${i18n}" />"
        class="button admin" />
    </form>
    <div>
      <img src="img/icon/applicants_icon.png" />
      <h1>
        <fmt:message key="i18n.title.applicants_managing" bundle="${i18n}" />
      </h1>
    </div>
    <h2>
      <fmt:message key="i18n.title.applicants_list" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.applicants.list" bundle="${i18n}" />
    </p>

    <c:choose>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.applicants.list" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <p class="info-message">
          <fmt:message key="i18n.info.admin.applicants.list_empty" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <div class="control-panel clearfix">
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-applicants-list" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-applicants-list" />
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
              <td><fmt:message key="i18n.label.birthdate" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.phone" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.address" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.school" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.year" bundle="${i18n}" /></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="appl" items="${requestScope.list}">
              <tr>
                <td>
                  <form action="Controller" method="post">
                    <input type="hidden" name="command" value="get-applicant" />
                    <input type="hidden" name="applicant-id" value="${appl.id}" />
                    <input type="submit" value="${appl.name}">
                  </form>
                </td>
                <td>${appl.birthdate}</td>
                <td>${appl.phone}</td>
                <td>${appl.address}</td>
                <td>${appl.school}</td>
                <td>${appl.gradYear}</td>
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