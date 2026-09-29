package com.example.recipebox.service;

import com.example.recipebox.entity.User;
import com.example.recipebox.exception.BusinessRuleException;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.MealPlanRepository;
import com.example.recipebox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final MealPlanRepository mealPlanRepository;

    public UserService(UserRepository repository, MealPlanRepository mealPlanRepository) {
        this.repository = repository;
        this.mealPlanRepository = mealPlanRepository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Transactional
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BusinessRuleException("Name is required.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessRuleException("Email is required.");
        }
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        if (repository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new BusinessRuleException("Email already registered.");
        }
        return repository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        // Meal plans belong to the user, so remove them explicitly before deleting the user.
        mealPlanRepository.deleteByUser_Id(id);
        repository.deleteById(id);
    }
}
