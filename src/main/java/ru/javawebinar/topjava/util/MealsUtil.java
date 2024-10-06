package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {

    public static List<MealTo> filteredByStream(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesGroupByLocalDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
                );
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal ->
                        mealToMealTo(meal, caloriesGroupByLocalDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<MealTo> mealToMealTo(List<Meal> meals, int caloriesPerDay) {
        return filteredByStream(meals, LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
    }

    public static MealTo mealToMealTo(Meal meal, boolean exceed) {
        return new MealTo(
                meal.getId(),
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                exceed
        );
    }
}