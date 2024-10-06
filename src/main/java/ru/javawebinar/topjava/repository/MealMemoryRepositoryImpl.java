package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MealMemoryRepositoryImpl implements MealRepository {

    private Map<Long, Meal> storage = new HashMap<>();
    private final AtomicLong indexCounter = new AtomicLong(1);

    {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
       meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.getId() == null) {
            meal.setId(indexCounter.incrementAndGet());
            storage.put(meal.getId(), meal);
        }
        return storage.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public Iterable<Meal> findAll() {
        return storage.values();
    }

    @Override
    public synchronized Optional<Meal> findById(Long id) {
        return storage.containsKey(id) ? Optional.of(storage.get(id)) : Optional.empty();
    }

    @Override
    public synchronized void deleteById(Long id) {
        storage.remove(id);
    }

}
