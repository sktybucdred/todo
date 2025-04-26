package org.example.dziennikmonolith.service;

import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDetails.getName());
                    return categoryRepository.save(existingCategory);
                });
    }
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findByName(name);
    }

}
