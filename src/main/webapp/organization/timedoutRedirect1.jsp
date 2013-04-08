<%
    int timeout = session.getMaxInactiveInterval();
    response.setHeader("Refresh", timeout + "; URL = signin.jsp?message=timeout");
%>