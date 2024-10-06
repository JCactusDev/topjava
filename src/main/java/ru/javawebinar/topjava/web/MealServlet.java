package ru.javawebinar.topjava.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        log.debug("Init MealServlet");
        service = new MealServiceImpl();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("MealServlet: Process GET request");
        final String action = request.getParameter("action");
        switch (action == null ? "default" : action) {
            case "new":
                doGetActionNew(request, response);
                break;
            case "get":
                doGetActionGetById(request, response);
                break;
            case "delete":
                doGetActionDelete(request, response);
                break;
            default:
                doGetActionDefault(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("MealServlet: Process POST request");
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal();
        fillMealWithRequest(meal, request);
        final String paramId = request.getParameter("id");
        if(!paramId.isEmpty()) {
            meal.setId(Long.parseLong(paramId));
        }
        log.info("MealServlet: save meal with id " + paramId);
        service.save(meal);
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
        log.info("MealServlet: get meal with id {}", paramId);
        Meal meal = service.findById(id);
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("/WEB-INF/jsp/meals/edit.jsp").forward(request, response);
    }

    protected void doGetActionDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String paramId = request.getParameter("id");
        final Long id = Long.parseLong(paramId);
        log.info("MealServlet: delete meal with id {}", paramId);
        service.deleteById(id);
        response.sendRedirect("meals");
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

