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
  <section>
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
      <li class="selected"><fmt:message key="i18n.button.disciplines" bundle="${i18n}" /></li>
      <li>
        <form action="Controller">
          <input type="hidden" name="command" value="to-add-discipline-page" />
          <input type="submit"
            value="<fmt:message key="i18n.button.add_discipline" bundle="${i18n}" />" />
        </form>
      </li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.disciplines" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.disciplines" bundle="${i18n}" />
    </p>
    <c:choose>
      <c:when test="${requestScope.error}">
        <div>
          <p class="error-message">
            <fmt:message key="i18n.error.admin.disciplines.list" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:when test="${empty requestScope.list}">
        <div>
          <p class="info-message">
            <fmt:message key="i18n.info.admin.disciplines.list_empty" bundle="${i18n}" />
          </p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="control-panel clearfix">
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-disciplines-list" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="false" />
            <input type="submit" value="<fmt:message key="i18n.button.previous" bundle="${i18n}" />"
              class="button" />
          </form>
          <span>${requestScope.requiredPage}/${requestScope.pagesNumber}</span>
          <form action="Controller" method="post">
            <input type="hidden" name="command" value="get-disciplines-list" />
            <input type="hidden" name="current-page" value="${requestScope.requiredPage}" />
            <input type="hidden" name="next" value="true" />
            <input type="submit" value="<fmt:message key="i18n.button.next" bundle="${i18n}" />"
              class="button" />
          </form>
        </div>
        <table>
          <thead>
            <tr>
              <td><fmt:message key="i18n.label.discipline_title" bundle="${i18n}" /></td>
              <td><fmt:message key="i18n.label.faculties" bundle="${i18n}" /></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="discipline" items="${requestScope.list}">
              <tr>
                <td>
                  <form action="Controller">
                    <input type="hidden" name="command" value="get-discipline" />
                    <input type="hidden" name="discipline-id" value="${discipline.id}" />
                    <input type="submit" value="${discipline.title}"
                      title="<fmt:message key="i18n.button.edit" bundle="${i18n}" />">
                  </form>
                </td>
                <td><c:forEach var="faculty" items="${discipline.faculties}">
            ${faculty.title}<br />
                  </c:forEach></td>
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