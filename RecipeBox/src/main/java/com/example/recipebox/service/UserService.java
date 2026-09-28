package com.example.recipebox.service;

import com.example.recipebox.entity.User;
import com.example.recipebox.exception.BusinessRuleException;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { this.repository = repository; }
    public List<User> findAll() { return repository.findAll(); }
    public User findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)); }
    public User create(User user) {
        if (repository.existsByEmailIgnoreCase(user.getEmail())) throw new BusinessRuleException("Email already registered.");
        return repository.save(user);
    }
}
