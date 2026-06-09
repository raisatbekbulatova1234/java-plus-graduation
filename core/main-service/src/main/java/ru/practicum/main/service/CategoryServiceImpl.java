package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.error.EntityAlreadyExistsException;
import ru.practicum.main.error.EntityDeletedException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (!categoryRepository.existsByNameIgnoreCaseAndTrim(newCategoryDto.getName())) {
            return categoryMapper.toDto(categoryRepository
                    .save(categoryMapper.toCategory(newCategoryDto)));
        } else {
            throw new EntityAlreadyExistsException("Category", "name", newCategoryDto.getName());
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Id", categoryId));

        if (categoryRepository.existsByNameIgnoreCaseAndTrim(newCategoryDto.getName()) &&
                !category.getName().equalsIgnoreCase(newCategoryDto.getName())) {
            throw new EntityAlreadyExistsException("Category", "name", newCategoryDto.getName());
        }

        if (newCategoryDto.getName() != null && !newCategoryDto.getName().isBlank()) {
            category.setName(newCategoryDto.getName());
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {

        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new EntityNotFoundException("Category", "Id", categoryId);
        }
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new EntityDeletedException("Category", "name", categoryId);
        } else {
            categoryRepository.deleteById(categoryId);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Id", categoryId));
        return categoryMapper.toDto(category);
    }

}
