package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.main.dto.CategoryDto;
import ru.practicum.explorewithme.main.dto.NewCategoryDto;
import ru.practicum.explorewithme.main.model.Category;

/**
 * MapStruct маппер для сущности Category
 * Преобразует Category <-> CategoryDto <-> NewCategoryDto
 */
@Mapper(componentModel = "spring")  // Регистрирует маппер как Spring бин
public interface CategoryMapper {

    /**
     * Преобразует Category -> CategoryDto
     * Все поля копируются автоматически (id, name)
     */
    CategoryDto toDto(Category category);

    /**
     * Преобразует Long id в Category (только с заполненным id)
     * Используется для связей, когда нужно сослаться на существующую категорию
     */
    default Category fromId(Long id) {
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);  // Заполняем только id, остальные поля null
        return category;
    }

    /**
     * Преобразует NewCategoryDto -> Category
     * id игнорируется (генерируется БД при сохранении)
     */
    @Mapping(target = "id", ignore = true)  // Не копировать id из DTO
    Category toCategory(NewCategoryDto newCategoryDto);
}