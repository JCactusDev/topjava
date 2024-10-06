package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Optional;

public interface MealRepository {

    Meal save(Meal meal);

    Iterable<Meal> findAll();

    Optional<Meal> findById(Long id);

    void deleteById(Long id);

}
