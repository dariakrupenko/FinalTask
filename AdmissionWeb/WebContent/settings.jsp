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
<title><fmt:message key="i18n.head.settings" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="WEB-INF/jspf/header.jspf"%>
  <main>
  <section class="settings-panel">
    <div>
      <img src="img/icon/settings_icon.png" />
      <h1>
        <fmt:message key="i18n.title.settings" bundle="${i18n}" />
      </h1>
    </div>
    <div>
      <p>
        <fmt:message key="i18n.info.settings" bundle="${i18n}" />
      </p>
      <c:if test="${requestScope.successCompleted}">
        <p class="success-message">
          <fmt:message key="i18n.success.settings" bundle="${i18n}" />
        </p>
      </c:if>
    </div>
    <form action="Controller" class="form-box">
      <input type="hidden" name="command" value="accept-settings" />
      <div>
        <label>
          <fmt:message key="i18n.label.interface_language" bundle="${i18n}" />
          :
        </label>
        <select name="lang">
          <c:choose>
            <c:when test="${sessionScope.locale eq 'en'}">
              <option value="ru"><fmt:message key="i18n.label.ru" bundle="${i18n}" /></option>
              <option value="en" selected><fmt:message key="i18n.label.en" bundle="${i18n}" /></option>
            </c:when>
            <c:otherwise>
              <option value="ru" selected><fmt:message key="i18n.label.ru" bundle="${i18n}" /></option>
              <option value="en"><fmt:message key="i18n.label.en" bundle="${i18n}" /></option>
            </c:otherwise>
          </c:choose>
        </select>
      </div>
      <div>
        <label>
          <fmt:message key="i18n.label.elements_number" bundle="${i18n}" />
          :
        </label>
        <adm:formatSelect variants="5,10,15,20" />
      </div>
      <input type="submit" value="<fmt:message key="i18n.button.accept" bundle="${i18n}" />"
        class="button" />
    </form>
  </section>
  </main>
  <%@include file="WEB-INF/jspf/footer.jspf"%>
</body>
</html>