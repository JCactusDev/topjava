package ru.javawebinar.topjava.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MealServiceImpl;
import ru.javawebinar.topjava.util.MealsUtil;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000;

    private MealService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        service = new MealServiceImpl();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String action = request.getParameter("action");
        switch (action == null ? "default" : action) {
            case "new":
                doGetActionNew(request, response);
                break;
            case "get":
                doGetActionGetById(request, response);
                break;
            case "delete":
                doDelete(request, response);
                break;
            default:
                doGetActionDefault(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Meal meal = new Meal();
        fillMealWithRequest(meal, request);

        final String paramId = request.getParameter("id");
        if(!paramId.isEmpty()) {
            meal.setId(Long.parseLong(paramId));
        }

        meal = service.save(meal);
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("/WEB-INF/jsp/meals/edit.jsp").forward(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String paramId = request.getParameter("id");
        final Long id = Long.parseLong(paramId);
        service.deleteById(id);
        response.sendRedirect("meals");
    }

    private void doGetActionDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("meals", MealsUtil.mealToMealTo(service.findAll(), CALORIES_PER_DAY));
        request.getRequestDispatcher("/WEB-INF/jsp/meals/list.jsp").forward(request, response);
    }

    private void doGetActionNew(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("meal", new Meal());
        request.getRequestDispatcher("/WEB-INF/jsp/meals/edit.jsp").forward(request, response);
    }

    private void doGetActionGetById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String paramId = request.getParameter("id");
        final Long id = Long.parseLong(paramId);
        Meal meal = service.findById(id);
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("/WEB-INF/jsp/meals/edit.jsp").forward(request, response);
    }

    private void fillMealWithRequest(Meal meal, HttpServletRequest request) {
        final String paramDateTime = request.getParameter("dateTime");
        final LocalDateTime dateTime = LocalDateTime.parse(paramDateTime);
        meal.setDateTime(dateTime);

        final String paramDescription = request.getParameter("description");
        meal.setDescription(paramDescription);

        final String paramCalories= request.getParameter("calories");
        final int calories = Integer.parseInt(paramCalories);
        meal.setCalories(calories);
    }

}

