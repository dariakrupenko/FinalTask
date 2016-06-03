<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="${initParam.i18n}" var="i18n" />

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
  isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/admission.css" />
<link rel="shortcut icon" href="img/icon/shortcut.png" />
<title><fmt:message key="i18n.head.error" bundle="${i18n}" /></title>
</head>
<body>
  <%@include file="../jspf/header.jspf"%>
  <main>
  <section class="error-panel">
    <h2>
      <fmt:message key="i18n.error.database_error" bundle="${i18n}" />
    </h2>
  </section>
  </main>
  <%@include file="../jspf/footer.jspf"%>
</body>
</html>