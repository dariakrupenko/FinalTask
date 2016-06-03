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
  <%@include file="WEB-INF/jspf/header.jspf"%>
  <main>
  <section class="faculties-panel">
    <div>
      <img src="img/icon/faculties_list_icon.png" />
      <h1>
        <fmt:message key="i18n.title.faculties_list" bundle="${i18n}" />
      </h1>
    </div>
    <div>
      <p>
        <fmt:message key="i18n.info.faculties_list" bundle="${i18n}" />
      </p>
    </div>
    <c:choose>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.faculties.list" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <p class="info-message">
          <fmt:message key="i18n.info.admin.faculties.list_empty" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <div class="control-panel clearfix">
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-faculties-list" />
            <input type="hidden" name="for-admin" value="false" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-faculties-list" />
            <input type="hidden" name="for-admin" value="false" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="true" />
            <input type="submit" value="<fmt:message key="i18n.button.next" bundle="${i18n}" />"
              class="button" />
          </form>
        </div>
        <div class="faculties-list">
          <c:forEach var="faculty" items="${requestScope.list}">
            <div>
              <img src="img/logo/${faculty.logoname}" alt="fac icon" class="faculty-logo" />
              <form action="Controller">
                <input type="hidden" name="command" value="get-faculty" />
                <input type="hidden" name="for-admin" value="false" />
                <input type="hidden" name="faculty-id" value="${faculty.id}">
                <input type="submit" value="${faculty.title}" class="title" />
              </form>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="WEB-INF/jspf/footer.jspf"%>
</body>
</html>