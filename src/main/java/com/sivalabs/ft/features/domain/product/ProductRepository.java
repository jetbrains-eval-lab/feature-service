package com.sivalabs.ft.features.domain.product;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
    Optional<Product> findByCode(String code);
}
