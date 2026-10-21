<%@ tag description="Star Rating Component" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="rating" type="java.lang.Double" required="true" %>
<%@ attribute name="showNumber" type="java.lang.Boolean" required="false" %>

<div class="d-inline-flex align-items-center gap-1">
    <span class="ss-stars">
        <c:forEach begin="1" end="5" var="i">
            <i class="bi ${i <= rating ? 'bi-star-fill' : (i - 0.5 <= rating ? 'bi-star-half' : 'bi-star')}"></i>
        </c:forEach>
    </span>
    <c:if test="${showNumber}">
        <span class="fw-bold small text-dark ms-1">${rating} / 5.0</span>
    </c:if>
</div>
