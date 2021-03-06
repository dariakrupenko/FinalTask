<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="${initParam.i18n}" var="i18n" />

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
      <li><form action="Controller"  method="post">
          <input type="hidden" name="command" value="get-faculties-list" />
          <input type="hidden" name="for-admin" value="true" />
          <input type="submit"
            value="<fmt:message key="i18n.button.faculties" bundle="${i18n}" />" />
        </form></li>
      <li class="selected"><fmt:message key="i18n.button.add_faculty" bundle="${i18n}" /></li>
    </ul>

    <h2>
      <fmt:message key="i18n.title.faculty_addition" bundle="${i18n}" />
    </h2>
    <p>
      <fmt:message key="i18n.info.admin.faculties.add" bundle="${i18n}" />
    </p>
    <div>
      <c:if test="${requestScope.error}">
        <p>
          <fmt:message key="i18n.error.admin.faculties.add" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.validationFailed}">
      <p class="error-message">
        <fmt:message key="i18n.error.validate" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.alreadyExists}">
        <p class="error-message">
          <fmt:message key="i18n.error.admin.faculties.exist" bundle="${i18n}" />
        </p>
      </c:if>
      <c:if test="${requestScope.successAdded}">
        <p class="success-message">
          <fmt:message key="i18n.success.admin.faculties.add" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller"  method="post">
      <input type="hidden" name="command" value="add-faculty" />
      <input type="hidden" name="key" value="${applicationScope.generator.generatedKey}" />
      <h3>
        <fmt:message key="i18n.title.faculty_general_inf" bundle="${i18n}" />
      </h3>
      <div class="form-box">
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_title" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.title" bundle="${i18n}" />:</label>
          <input type="text" name="title" value="${requestScope.faculty.title}" maxLength="65"
            required />
        </div>
        <div>
          <label><fmt:message key="i18n.label.description" bundle="${i18n}" />:</label>
          <textarea name="description">${requestScope.faculty.description}</textarea>
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_logoname" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.logo" bundle="${i18n}" />:</label>
          <input type="text" name="logoname" value="${requestScope.faculty.logoname}" maxLength="10" />
        </div>
      </div>
      <h3>
        <fmt:message key="i18n.title.contacts_inf" bundle="${i18n}" />
      </h3>
      <div class="form-box">
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_phone" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.phone" bundle="${i18n}" />:</label>
          <input type="text" name="phone" value="${requestScope.faculty.phone}" maxLength="13"
            required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_address" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.address" bundle="${i18n}" />:</label>
          <input type="text" name="address" value="${requestScope.faculty.address}" maxLength="55"
            required />
        </div>
        <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculty_dean" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.dean_name" bundle="${i18n}" />:</label>
          <input type="text" name="dean" value="${requestScope.faculty.dean}" maxLength="40"
            required />
        </div>
      </div>
      <h3>
        <fmt:message key="i18n.title.disciplines_plan" bundle="${i18n}" />
      </h3>
      <div class="form-box">
      <div>
          <div></div>
          <div class="validate-message">
            <fmt:message key="i18n.validate.faculties_disciplines" bundle="${i18n}" />
          </div>
        </div>
        <div>
          <label><fmt:message key="i18n.label.disciplines" bundle="${i18n}" />:</label>
          <c:forEach var="d" items="${requestScope.dList}">
            <input type="checkbox" name="dId${d.id}" value="${d.title}">${d.title}</input>
            <br />
          </c:forEach>
        </div>
        <div>
          <label><fmt:message key="i18n.label.plan" bundle="${i18n}" />:</label>
          <input type="number" name="plan" value="${requestScope.faculty.plan}" />
        </div>
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.add" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="../../jspf/footer.jspf"%>
</body>
</html>