package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("==========================================================================================");

        List<UserMealWithExcess> mealsToStream = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStream.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        // Суммируем калории по дате без времени
        Map<LocalDate, Integer> caloriesGroupByLocalDate = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate currentLocalDate = meal.getDateTime().toLocalDate();
            caloriesGroupByLocalDate.put(currentLocalDate, (caloriesGroupByLocalDate.getOrDefault(currentLocalDate, 0)) + meal.getCalories());
        }

        // Проводим оценку и заполняем результат
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            int calories = caloriesGroupByLocalDate.get(meal.getDateTime().toLocalDate());
            result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), calories > caloriesPerDay));
        }

        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams

        // Суммируем калории по дате без времени
        Map<LocalDate, Integer> caloriesGroupByLocalDate = meals.stream().collect(
                Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories))
        );

        // Проводим оценку и заполняем и возвращаем результат
        return meals
                .stream()
                .map(meal ->
                        new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), caloriesGroupByLocalDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)
                )
                .toList();
    }
}
