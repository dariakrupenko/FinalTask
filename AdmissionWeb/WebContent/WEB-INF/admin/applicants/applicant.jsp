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
  <section>
    <form action="Controller">
      <input type="hidden" name="command" value="to-admin-login-page" />
      <input type="submit"
        value="<fmt:message key="i18n.button.return_to_admin" bundle="${i18n}" />"
        class="button admin" />
    </form>
    <div>
      <img src="img/icon/applicants_icon.png" />
      <h1>
        <fmt:message key="i18n.title.applicant_review" bundle="${i18n}" />
      </h1>
    </div>
    <c:choose>
      <c:when test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.applicants.review" bundle="${i18n}" />
        </p>
      </c:when>
      <c:when test="${requestScope.notFound}">
        <p class="info-message">
          <fmt:message key="i18n.info.admin.applicants.review_empty" bundle="${i18n}" />
        </p>
      </c:when>
      <c:otherwise>
        <h3>
          <fmt:message key="i18n.title.personal_inf" bundle="${i18n}" />
        </h3>
        <div class="form-box">
          <div>
            <label>
              <fmt:message key="i18n.label.name" bundle="${i18n}" />
              :
            </label>
            <strong>${requestScope.appl.name}</strong>
          </div>
          <div>
            <label>E-mail:</label>
            ${requestScope.appl.email}
          </div>
          <div>
            <label>
              <fmt:message key="i18n.label.birthdate" bundle="${i18n}" />
              :
            </label>
            ${requestScope.appl.birthdate}
          </div>
          <div>
            <label>
              <fmt:message key="i18n.label.phone" bundle="${i18n}" />
              :
            </label>
            ${requestScope.appl.phone}
          </div>
          <div>
            <label>
              <fmt:message key="i18n.label.address" bundle="${i18n}" />
              :
            </label>
            ${requestScope.appl.address}
          </div>
          <div>
            <label>
              <fmt:message key="i18n.label.school" bundle="${i18n}" />
              :
            </label>
            ${requestScope.appl.school}
          </div>
          <div>
            <label>
              <fmt:message key="i18n.label.year" bundle="${i18n}" />
              :
            </label>
            ${requestScope.appl.gradYear}
          </div>
        </div>
        <c:choose>
          <c:when test="${empty requestScope.appl.record}">
            <p class="info-message">
              <fmt:message key="i18n.info.admin.applicants.empty_record" bundle="${i18n}" />
            </p>
          </c:when>
          <c:otherwise>
            <h3>
              <fmt:message key="i18n.title.score_inf" bundle="${i18n}" />
            </h3>
            <div class="form-box">
              <c:forEach var="score" items="${requestScope.appl.record.scores}">
                <div>
                  <label>${score.key.title}:</label>
                  ${score.value}
                </div>
              </c:forEach>
              <div>
                <label>
                  <fmt:message key="i18n.label.certificate" bundle="${i18n}" />
                  :
                </label>
                ${requestScope.appl.record.certificateScore}
              </div>
              <div>
                <label>
                  <fmt:message key="i18n.label.total_score" bundle="${i18n}" />
                  :
                </label>
                ${requestScope.appl.record.totalScore}
              </div>
            </div>
            <h3>
              <fmt:message key="i18n.title.status" bundle="${i18n}" />
            </h3>
            <div class="form-box">
              <div>
                <label>
                  <fmt:message key="i18n.label.faculty" bundle="${i18n}" />
                  :
                </label>
                ${requestScope.appl.record.faculty.title}
              </div>
              <div>
                <label>
                  <fmt:message key="i18n.label.status" bundle="${i18n}" />
                  :
                </label>
                <fmt:message
                  key="i18n.info.applicant.status.${requestScope.appl.record.status}"
                  bundle="${i18n}" />
              </div>
            </div>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>