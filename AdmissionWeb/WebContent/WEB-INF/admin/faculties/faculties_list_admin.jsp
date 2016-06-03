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
  <section>
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
      <li class="selected"><fmt:message key="i18n.button.faculties" bundle="${i18n}" /></li>
      <li><form action="Controller">
          <input type="hidden" name="command" value="to-add-faculty-page" />
          <input type="submit"
            value="<fmt:message key="i18n.button.add_faculty" bundle="${i18n}" />" />
        </form></li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.faculties" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.faculties" bundle="${i18n}" />
    </p>
    <c:choose>
      <c:when test="${requestScope.error}">
        <div>
          <p class="error-message">
            <fmt:message key="i18n.error.admin.faculties.list" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <div>
          <p class="info-message">
            <fmt:message key="i18n.info.admin.faculties.list_empty" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="control-panel clearfix">
          <form action="Controller"  method="post">
            <input type="hidden" name="command" value="get-faculties-list" />
            <input type="hidden" name="for-admin" value="true" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller"  method="post">
            <input type="hidden" name="command" value="get-faculties-list" />
            <input type="hidden" name="for-admin" value="true" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="true" />
            <input type="submit" value="<fmt:message key="i18n.button.next" bundle="${i18n}" />"
              class="button" />
          </form>
        </div>
        <table>
          <thead>
            <tr>
              <td><fmt:message key="i18n.label.faculty" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.plan" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.pass_rate" bundle="${i18n}" /></td>
              <td class="center"><fmt:message key="i18n.label.applicants_number"
                  bundle="${i18n}" /></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="faculty" items="${requestScope.list}">
              <tr>
                <td>
                  <form action="Controller">
                    <input type="hidden" name="command" value="get-faculty" />
                    <input type="hidden" name="faculty-id" value="${faculty.id}" />
                    <input type="hidden" name="for-admin" value="true" />
                    <input type="submit" value="${faculty.title}"
                      title="<fmt:message key="i18n.button.edit" bundle="${i18n}" />">
                  </form>
                </td>
                <td class="center">${faculty.plan}</td>
                <td class="center">${faculty.passRate}</td>
                <td class="center">${faculty.applicantsCount}</td>
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