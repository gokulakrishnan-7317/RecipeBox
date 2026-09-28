package com.example.recipebox.repository;

import com.example.recipebox.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("select distinct r from Recipe r left join r.ingredients i where lower(r.title) like lower(concat('%', :q, '%')) or lower(coalesce(r.cuisine,'')) like lower(concat('%', :q, '%')) or lower(i.name) like lower(concat('%', :q, '%'))")
    List<Recipe> search(@Param("q") String q);
    List<Recipe> findByFavoriteTrueOrderByTitleAsc();
}
