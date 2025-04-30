package com.sivalabs.ft.features.domain.product;

import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
    Optional<Product> findByCode(String code);
}
