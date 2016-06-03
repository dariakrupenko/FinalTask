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
      <li class="selected"><fmt:message key="i18n.button.enrolls" bundle="${i18n}" /></li>
      <li>
        <form action="Controller">
          <input type="hidden" name="command" value="to-start-enroll-page" />
          <input type="submit"
            value="<fmt:message key="i18n.button.open_enroll" bundle="${i18n}" />" />
        </form>
      </li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.enrolls" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.enrollment" bundle="${i18n}" />
    </p>
    <c:choose>
      <c:when test="${requestScope.error}">
        <div>
          <p class="error-message">
            <fmt:message key="i18n.error.admin.enrollment.list" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <div>
          <p class="info-message">
            <fmt:message key="i18n.info.admin.enrollment.list_empty" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:otherwise>
        <c:if test="${requestScope.errorDeleted}">
          <p class="error-message">
            <fmt:message key="i18n.error.admin.enrollment.delete" bundle="${i18n}" />
          </p>
        </c:if>
        <c:if test="${requestScope.noCurrentEnroll}">
          <p class="error-message">
            <fmt:message key="i18n.error.admin.enrollment.complete_no_enroll" bundle="${i18n}" />
          </p>
        </c:if>
        <c:if test="${requestScope.deleteUnable}">
          <p class="error-message">
            <fmt:message key="i18n.error.admin.enrollment.delete_current_enroll" bundle="${i18n}" />
          </p>
        </c:if>
        <c:if test="${requestScope.successDeleted}">
          <p class="success-message">
            <fmt:message key="i18n.success.admin.enrollment.delete" bundle="${i18n}" />
          </p>
        </c:if>
        <c:if test="${requestScope.successCompleted}">
          <p class="success-message">
            <fmt:message key="i18n.success.admin.enrollment.complete" bundle="${i18n}" />
          </p>
        </c:if>
        <div class="control-panel clearfix">
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-enrolls-list" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-enrolls-list" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="true" />
            <input type="submit" value="<fmt:message key="i18n.button.next" bundle="${i18n}" />"
              class="button" />
          </form>
        </div>
        <table>
          <thead>
            <tr>
              <td><fmt:message key="i18n.label.begin_date" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.end_date" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.status" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.action" bundle="${i18n}" /></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="enroll" items="${requestScope.list}">
              <tr>
                <td>${enroll.beginDate}</td>
                <td>${enroll.endDate}</td>
                <td class="center"><c:if test="${enroll.status}">
                    <fmt:message key="i18n.info.admin.enrollment.active" bundle="${i18n}" />
                  </c:if> <c:if test="${!enroll.status}">
                    <fmt:message key="i18n.info.admin.enrollment.finished" bundle="${i18n}" />
                  </c:if></td>
                <td class="center">
                  <form action="Controller">
                    <input type="hidden" name="command" value="delete-enroll" />
                    <input type="hidden" name="enroll-id" value="${enroll.id}" />
                    <input type="submit"
                      value="<fmt:message key="i18n.button.delete" bundle="${i18n}" />"
                      class="button table-button" />
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
        <form action="Controller">
          <input type="hidden" name="command" value="complete-enroll" />
          <input type="submit"
            value="<fmt:message key="i18n.button.complete_enroll" bundle="${i18n}" />"
            class="button admin" />
        </form>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>