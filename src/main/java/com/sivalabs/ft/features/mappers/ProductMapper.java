package com.sivalabs.ft.features.mappers;

import com.sivalabs.ft.features.api.dtos.ProductDto;
import com.sivalabs.ft.features.domain.product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
}
