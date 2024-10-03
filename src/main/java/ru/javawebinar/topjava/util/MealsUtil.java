package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        List<MealTo> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("==========================================================================================");
        List<MealTo> mealsToStreams = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStreams.forEach(System.out::println);

        System.out.println("==========================================================================================");
        List<MealTo> mealsToStream = filteredByStreamON(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStream.forEach(System.out::println);

        System.out.println("==========================================================================================");
        List<MealTo> mealsToStreamInnerClass = filteredByStreamInnerClass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStreamInnerClass.forEach(System.out::println);
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesGroupByLocalDate = new HashMap<>();
        for (Meal meal : meals) {
            caloriesGroupByLocalDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }
        List<MealTo> result = new ArrayList<>();
        for (Meal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                result.add(new MealTo(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesGroupByLocalDate.get(meal.getDate()) > caloriesPerDay)
                );
            }
        }
        return result;
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesGroupByLocalDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
                );
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal ->
                        new MealTo(
                                meal.getDateTime(),
                                meal.getDescription(),
                                meal.getCalories(),
                                caloriesGroupByLocalDate.get(meal.getDate()) > caloriesPerDay
                        )
                )
                .collect(Collectors.toList());
    }

    public static List<MealTo> filteredByStreamON(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate))
                .values()
                .stream()
                .flatMap(dayMeals -> {
                    boolean exceed = dayMeals.stream()
                            .mapToInt(Meal::getCalories).sum() > caloriesPerDay;
                    return dayMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                            .map(meal -> new MealTo(
                                            meal.getDateTime(),
                                            meal.getDescription(),
                                            meal.getCalories(),
                                            exceed
                                    )
                            );
                })
                .collect(Collectors.toList());
    }

    public static List<MealTo> filteredByStreamInnerClass(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final class Aggregate {
            private final List<Meal> meals = new ArrayList<>();
            private int dailySumOfCalories;

            private void accumulate(Meal meal) {
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

            private Stream<MealTo> finisher() {
                final boolean exceed = dailySumOfCalories > caloriesPerDay;
                return meals.stream()
                        .map(meal -> new MealTo(
                                        meal.getDateTime(),
                                        meal.getDescription(),
                                        meal.getCalories(),
                                        exceed
                                )
                        );
            }
        }

        return meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate,
                        Collector.of(Aggregate::new, Aggregate::accumulate, Aggregate::combine, Aggregate::finisher))
                )
                .values().stream()
                .flatMap(t -> t)
                .collect(Collectors.toList());
    }
}