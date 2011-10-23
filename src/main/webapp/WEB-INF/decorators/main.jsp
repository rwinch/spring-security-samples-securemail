<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:decorator="http://www.opensymphony.com/sitemesh/decorator"
    xmlns:page="http://www.opensymphony.com/sitemesh/page"
    xmlns:form="http://www.springframework.org/tags/form"
    xmlns:spring="http://www.springframework.org/tags"
    xmlns:sec="http://www.springframework.org/security/tags"
    xmlns:tags="urn:jsptagdir:/WEB-INF/tags" version="2.0">

  <jsp:directive.page contentType="text/html" pageEncoding="UTF-8" />
  <jsp:output omit-xml-declaration="true" />
  <jsp:output doctype-root-element="HTML"
              doctype-system="about:legacy-compat" />
<html>
    <head>
        <title>SecureMail: <decorator:title/></title>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
        <c:url var="mainUrl" value="/resources/css/main.css"/>
        <link rel="stylesheet" href="${mainUrl}" type="text/css" media="screen" />
    </head>
    <body>
        <div id="header-container">
            <div id="header">
                <h1>
                    <c:url var="homeUrl" value="/"/>
                    <a title="SecureMail" href="${homeUrl}">
                        <c:url var="emailImgUrl" value="/resources/img/email.png"/>
                        <img src="${emailImgUrl}" alt="SecureMail Logo" title="SecureMail Logo"/>
                    </a>
                    SecureMail
                </h1>
            </div>
        </div>
        <div id="nav-container">
            <div id="nav">
                <ul class="nav">
                    <sec:authorize access="authenticated" var="authenticated"/>
                    <c:choose>
                        <c:when test="${authenticated}">
                            <li>Welcome <sec:authentication property="principal.username" /></li>
                            <c:url var="logoutUrl" value="/logout"/>
                            <li><a href="${logoutUrl}">Logout</a></li>
                        </c:when>
                        <c:otherwise>
                            <c:url var="loginUrl" value="/login"/>
                            <li><a href="${loginUrl}">Login</a></li>
                            <c:url var="signupUrl" value="/signup"/>
                            <li><a href="${signupUrl}">Signup</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
        <div id="content-container">
            <div id="sub-nav">
                <ul class="nav">
                    <tags:tab url="/users/sessions" text="User Sessions" id="sessions"/>
                    <tags:tab url="/messages/inbox" text="Inbox" id="inbox"/>
                    <tags:tab url="/messages/?form" text="Compose" id="compose"/>
                    <tags:tab url="/messages/sent" text="Sent" id="sent"/>
                </ul>
            </div>
            <div id="content">
                <div id="sub-content">
                    <decorator:body/>
                </div>
            </div>
        </div>
        <div id="footer-container">
            <div id="footer">
                <a id="springsecurity" href="http://static.springsource.org/spring-security/site/">Spring Security</a>
            </div>
        </div>
    </body>
</html>
</jsp:root>