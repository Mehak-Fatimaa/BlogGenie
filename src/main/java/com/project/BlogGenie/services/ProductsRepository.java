package com.project.BlogGenie.services;

import com.project.BlogGenie.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// isy do chuzy chye ek product dsra uski id aur id int me hai islye integer
public interface ProductsRepository extends JpaRepository<Product, Integer> {
}
