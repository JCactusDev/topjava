<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ru">
    <head>
        <title>Meals</title>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h2>Meal list:</h2>
        <main>
            <a href="meals?action=new">Add Meal</a>
            <table border="1" cellpadding="8" cellspacing="0">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Calories</th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <jsp:useBean id="meals" type="java.util.List" scope="request" />
                <c:forEach items="${meals}" var="mealTo">
                    <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo" />
                    <tr style="color: ${mealTo.excess ? 'red' : 'green'}">
                        <td>
                            <fmt:parseDate value="${mealTo.dateTime}" pattern="yyyy-MM-dd'T'HH:mm"
                                           var="parsedDateTime"
                                           type="both"/>
                            <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateTime}"/></td>
                        </td>
                        <td>${mealTo.description}</td>
                        <td>${mealTo.calories}</td>
                        <td><a href="meals?action=get&id=${mealTo.id}">Update</a></td>
                        <td><a href="meals?action=delete&id=${mealTo.id}">Delete</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </main>
    </body>
</html>