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
<title><fmt:message key="i18n.head.applicant_registration" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="WEB-INF/jspf/header.jspf"%>
  <main>
  <section>
    <div>
      <img src="img/icon/registrate_icon.png" />
      <h1>
        <fmt:message key="i18n.title.applicant_registration" bundle="${i18n}" />
      </h1>
    </div>
    <div>
      <c:if test="${requestScope.error}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.registration" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.validationFailed}">
        <p class="error-message">
          <fmt:message key="i18n.error.validate" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.alreadyExists}">
        <p class="error-message">
          <fmt:message key="i18n.error.applicant.exist" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.successRegistrated}">
        <p class="success-message">
          <fmt:message key="i18n.success.applicant.registration" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller"  method="post">
      <input type="hidden" name="command" value="registrate-applicant" />
      <input type="hidden" name="key" value="${applicationScope.generator.generatedKey}" />
      <h3>
        <fmt:message key="i18n.title.data_for_account" bundle="${i18n}" />
      </h3>
      <div class="form-box">
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.appl_login" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.login" bundle="${i18n}" />:</label>
          <input type="text" name="login" value="${requestScope.appl.login}" maxLength="40" required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.appl_password" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.password" bundle="${i18n}" />:</label>
          <input type="password" name="password" maxLength="40"
            value="${requestScope.appl.password}" required />
        </div>
      </div>
      <h3>
        <fmt:message key="i18n.title.personal_inf" bundle="${i18n}" />
      </h3>
      <div class="form-box">
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.appl_name" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.name" bundle="${i18n}" />:</label>
          <input type="text" name="name" value="${requestScope.appl.name}" maxLength="40" required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.appl_email" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label>E-mail:</label>
          <input type="email" name="email" value="${requestScope.appl.email}" maxLength="30"
            required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.date_format" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.birthdate" bundle="${i18n}" />:</label>
          <input type="date" name="birthdate"
            value='<fmt:formatDate value="${requestScope.appl.birthdate}" pattern="yyyy-MM-dd"/>'
            required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_phone" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.phone" bundle="${i18n}" />:</label>
          <input type="text" name="phone" value="${requestScope.appl.phone}" maxLength="12" required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_address" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.address" bundle="${i18n}" />:</label>
          <input type="text" name="address" value="${requestScope.appl.address}" maxLength="55"
            required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.appl_school" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.school" bundle="${i18n}" />:</label>
          <input type="text" name="school" value="${requestScope.appl.school}" maxLength="70"
            required />
        </div>
        <div>
          <label><fmt:message key="i18n.label.year" bundle="${i18n}" />:</label>
          <input type="number" name="gradyear" value="${requestScope.appl.gradYear}" maxLength="4"
            required />
        </div>
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.registration" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="WEB-INF/jspf/footer.jspf"%>
</body>
</html>