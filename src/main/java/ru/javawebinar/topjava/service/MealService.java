package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal) {
        return repository.save(meal);
    }
    public void delete(int id) { checkNotFoundWithId(repository.delete(id), id); }
    public List<Meal> getAll() {
        return (List<Meal>) repository.getAll();
    }
    public void update(Meal meal) {
        checkNotFoundWithId(repository.save(meal), meal.getId());
    }

}