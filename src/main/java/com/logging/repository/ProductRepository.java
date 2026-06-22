package com.logging.repository;

import com.logging.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Gonuguntala Jaikar Ramu
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
