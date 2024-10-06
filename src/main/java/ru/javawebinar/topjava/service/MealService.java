package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealService {

    Meal save(Meal meal);

    List<Meal> findAll();

    Meal findById(Long id);

    void deleteById(Long id);

}
