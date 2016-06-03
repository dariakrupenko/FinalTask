<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tld/admissiontaglib.tld" prefix="adm"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="${initParam.i18n}" var="i18n" />

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/admission.css" />
<link rel="shortcut icon" href="img/icon/shortcut.png" />
<title><fmt:message key="i18n.head.applicant_account" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../jspf/header.jspf"%>
  <main>
  <section class="status-panel">
    <div>
      <img src="img/icon/personal_account_icon.png" />
      <h1>
        <fmt:message key="i18n.title.applicant_account" bundle="${i18n}" />
      </h1>
    </div>
    <ul>
      <li>
        <form action="Controller">
          <input type="hidden" name="command" value="enter-account" />
          <input type="submit"
            value="<fmt:message key="i18n.button.personal_inf" bundle="${i18n}" />" />
        </form>
      </li>
      <li class="selected"><fmt:message key="i18n.button.status" bundle="${i18n}" /></li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.status" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.applicant.status" bundle="${i18n}" />
    </p>
    <div>
      <c:if test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registry.cancel_error" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.deleteUnable}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registry.cancel" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <c:choose>
      <c:when test="${requestScope.notRegistered}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.empty_record" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.successDeleted}">
        <p class="success-message">
          <fmt:message key="i18n.success.applicant.registry.cancel" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <form action="Controller">
          <input type="hidden" name="command" value="cancel-registry-for-faculty" />
          <dl>
            <dt>
              <fmt:message key="i18n.label.current_enroll" bundle="${i18n}" />
            </dt>
            <dd>
              <fmt:formatDate value="${sessionScope.applicant.record.enroll.beginDate}"
                pattern="dd.MM.yyyy" />
              -
              <fmt:formatDate value="${sessionScope.applicant.record.enroll.endDate}"
                pattern="dd.MM.yyyy" />
            </dd>
            <dt>
              <fmt:message key="i18n.label.applicant_faculty" bundle="${i18n}" />
            </dt>
            <dd>
              <strong>${sessionScope.applicant.record.faculty.title}</strong>
            </dd>
            <dt>
              <fmt:message key="i18n.label.applicant_scores" bundle="${i18n}" />
            </dt>
            <dd>
              <c:forEach var="d" items="${sessionScope.applicant.record.scores}">
              ${d.key.title} : ${d.value}<br />
              </c:forEach>
              <fmt:message key="i18n.label.certificate" bundle="${i18n}" />
              : ${sessionScope.applicant.record.certificateScore}<br /> <strong><fmt:message
                  key="i18n.label.total_score" bundle="${i18n}" />:
                ${sessionScope.applicant.record.totalScore}</strong>
            </dd>
            <dt>
              <fmt:message key="i18n.label.pass_rate" bundle="${i18n}" />
            </dt>
            <dd>
              <strong>${sessionScope.applicant.record.faculty.passRate}</strong>
            </dd>
            <dt>
              <fmt:message key="i18n.label.status" bundle="${i18n}" />
            </dt>
            <dd>
              <strong> <fmt:message
                  key="i18n.info.applicant.status.${sessionScope.applicant.record.status}"
                  bundle="${i18n}" />
              </strong>

            </dd>
          </dl>
          <input type="submit" value="<fmt:message key="i18n.button.cancel" bundle="${i18n}" />"
            class="button" />
        </form>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../jspf/footer.jspf"%>
</body>
</html>