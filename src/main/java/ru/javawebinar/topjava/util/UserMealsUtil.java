package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<UserMealWithExcess> mealsToStreams = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStreams.forEach(System.out::println);

        System.out.println("==========================================================================================");
        List<UserMealWithExcess> mealsToStream = filteredByStreamON(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStream.forEach(System.out::println);

        System.out.println("==========================================================================================");
        List<UserMealWithExcess> mealsToStreamInnerClass = filteredByStreamInnerClass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStreamInnerClass.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesGroupByLocalDate = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesGroupByLocalDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesGroupByLocalDate.get(meal.getDate()) > caloriesPerDay)
                );
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesGroupByLocalDate = meals.stream()
                .collect(
                        Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))
                );
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal ->
                        new UserMealWithExcess(
                                meal.getDateTime(),
                                meal.getDescription(),
                                meal.getCalories(),
                                caloriesGroupByLocalDate.get(meal.getDate()) > caloriesPerDay
                        )
                )
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamON(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate))
                .values()
                .stream()
                .flatMap(dayMeals -> {
                    boolean exceed = dayMeals.stream()
                            .mapToInt(UserMeal::getCalories).sum() > caloriesPerDay;
                    return dayMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(
                                            meal.getDateTime(),
                                            meal.getDescription(),
                                            meal.getCalories(),
                                            exceed
                                    )
                            );
                })
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamInnerClass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final class Aggregate {
            private final List<UserMeal> meals = new ArrayList<>();
            private int dailySumOfCalories;

            private void accumulate(UserMeal meal) {
                dailySumOfCalories += meal.getCalories();
                if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                    meals.add(meal);
                }
            }

            private Aggregate combine(Aggregate thah) {
                this.meals.addAll(thah.meals);
                this.dailySumOfCalories += thah.dailySumOfCalories;
                return this;
            }

            private Stream<UserMealWithExcess> finisher() {
                final boolean exceed = dailySumOfCalories > caloriesPerDay;
                return meals.stream()
                        .map(meal -> new UserMealWithExcess(
                                        meal.getDateTime(),
                                        meal.getDescription(),
                                        meal.getCalories(),
                                        exceed
                                )
                        );
            }
        }

        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate,
                        Collector.of(Aggregate::new, Aggregate::accumulate, Aggregate::combine, Aggregate::finisher))
                )
                .values().stream()
                .flatMap(t -> t)
                .collect(Collectors.toList());
    }
}
