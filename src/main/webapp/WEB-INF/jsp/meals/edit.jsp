<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
    <head>
        <title>Meals</title>
        <link rel="stylesheet" href="styles/style.css">
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h2>Meal edit:</h2>
        <main>
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request" />
            <form class="edit_form grid_area" action="meals?id=${meal.id}" method="post">
                <label for="dateTime">DateTime</label>
                <input type="datetime-local" name="dateTime" id="dateTime" value="${meal.dateTime}">

                <label for="description">Description</label>
                <input type="text" name="description" id="description" value="${meal.description}">

                <label for="calories">Calories</label>
                <input type="number" name="calories" id="calories" value="${meal.calories}">

                <input type="submit" value="Save">
                <input type="reset" value="Cancel">
            </form>
        </main>
    </body>
</html>