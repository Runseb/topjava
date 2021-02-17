package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.getUserId() == null) meal.setUserId(SecurityUtil.authUserId());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        if (get(meal.getId()) == null) return null;
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        Meal meal = repository.get(id);
        if (meal!=null && meal.getUserId().equals(SecurityUtil.authUserId())) return repository.remove(id) != null;
        return false;
    }

    @Override
    public Meal get(int id) {
        if (repository.get(id)==null) return null;
        return repository.get(id).getUserId().equals(SecurityUtil.authUserId()) ? repository.get(id):null;
    }

    @Override
    public Collection<Meal> getAll() {
        return repository.values().stream().filter(m-> m.getUserId().equals(SecurityUtil.authUserId()))
                .sorted(((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime())))
                .collect(Collectors.toList());
    }
}

