package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealMemoryRepositoryImpl;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MealServiceImpl implements MealService {

    private final MealRepository repository;

    public MealServiceImpl() {
        repository = new MealMemoryRepositoryImpl();
    }

    @Override
    public Meal save(Meal meal) {
        return repository.save(meal);
    }

    @Override
    public List<Meal> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(Comparator.comparing(Meal::getId)).collect(Collectors.toList());
    }

    @Override
    public Meal findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
