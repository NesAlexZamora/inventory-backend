package com.company.inventory.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.company.inventory.model.Category;

public interface ICategoryDao extends CrudRepository<Category, Long>{
    List<Category> findByNameContainingIgnoreCase(String name);
}
