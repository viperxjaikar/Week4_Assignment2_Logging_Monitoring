package com.logging.service;

import com.logging.exception.ResourceNotFoundException;
import com.logging.model.Product;
import com.logging.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Product Service with SLF4J logging at every operation.
 * Demonstrates DEBUG, INFO, WARN, ERROR log levels.
 *
 * @author Gonuguntala Jaikar Ramu
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        logger.info("Creating product: name={}, category={}, price={}",
                product.getName(), product.getCategory(), product.getPrice());

        Product saved = productRepository.save(product);

        logger.info("Product created successfully with ID: {}", saved.getId());
        return saved;
    }

    public List<Product> getAllProducts() {
        logger.debug("Fetching all products from database");

        List<Product> products = productRepository.findAll();

        logger.info("Retrieved {} products", products.size());
        return products;
    }

    public Product getProductById(Long id) {
        logger.debug("Fetching product with ID: {}", id);

        return productRepository.findById(id).orElseThrow(() -> {
            logger.error("Product not found with ID: {}", id);
            return new ResourceNotFoundException("Product not found with ID: " + id);
        });
    }

    public Product updateProduct(Long id, Product details) {
        logger.info("Updating product with ID: {}", id);

        Product product = getProductById(id);

        logger.debug("Old values - name: {}, price: {}", product.getName(), product.getPrice());

        product.setName(details.getName());
        product.setCategory(details.getCategory());
        product.setPrice(details.getPrice());
        product.setStock(details.getStock());

        Product updated = productRepository.save(product);

        logger.info("Product updated successfully. ID: {}, New name: {}, New price: {}",
                id, updated.getName(), updated.getPrice());
        return updated;
    }

    public void deleteProduct(Long id) {
        logger.warn("Deleting product with ID: {}", id);

        Product product = getProductById(id);
        productRepository.delete(product);

        logger.info("Product deleted successfully. ID: {}", id);
    }
}
