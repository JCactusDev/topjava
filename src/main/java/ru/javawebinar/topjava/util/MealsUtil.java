package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {

    public static List<MealTo> filteredByStream(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate))
                .values()
                .stream()
                .flatMap(dayMeals -> {
                    boolean exceed = dayMeals.stream()
                            .mapToInt(Meal::getCalories).sum() > caloriesPerDay;
                    return dayMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                            .map(meal -> mealToMealTo(meal, exceed)
                    );
                })
                .sorted(Comparator.comparing(MealTo::getDateTime))
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