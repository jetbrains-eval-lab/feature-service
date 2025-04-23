package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import com.sivalabs.ft.features.api.dtos.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ProductControllerTests extends AbstractIT {

    @Test
    void shouldGetAllProducts() {
        var result = mvc.get().uri("/api/products");
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.size()")
                .asNumber()
                .satisfies(size -> assertThat(size.intValue()).isGreaterThanOrEqualTo(5));
    }

    @Test
    void shouldGetProductByCode() {
        var expected = new ProductDto(
                1L,
                "intellij",
                "IntelliJ IDEA",
                "JetBrains IDE for Java",
                "https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png",
                false,
                "admin");
        var actual = mvc.get().uri("/api/products/{code}", "intellij");
        assertThat(actual)
                .hasStatusOk()
                .bodyJson()
                .convertTo(ProductDto.class)
                .usingRecursiveComparison()
                .comparingOnlyFields("code", "name", "description", "imageUrl", "disabled", "createdBy")
                .isEqualTo(expected);
    }

    @Test
    void shouldReturn404WhenProductNotFound() {
        var actual = mvc.get().uri("/api/products/{code}", "INVALID_CODE");
        assertThat(actual).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldCreateNewProduct() {
        var payload =
                """
            {
                "code": "rover",
                "name": "Rust Rover",
                "description": "JetBrains Rust Rover",
                "imageUrl": "https://resources.jetbrains.com/storage/products/company/brand/logos/RustRover.png"
            }
            """;

        var result = mvc.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        assertThat(result).hasStatus(HttpStatus.CREATED);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldUpdateProduct() {
        var payload =
                """
            {
                "name": "IntelliJ IDEA Ultimate",
                "description": "Best IDE for Java",
                "imageUrl": "https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_Ultimate.png"
            }
            """;

        var result = mvc.put()
                .uri("/api/products/{code}", "intellij")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        assertThat(result).hasStatusOk();

        // Verify the update
        var expected = new ProductDto(
                1L,
                "intellij",
                "IntelliJ IDEA Ultimate",
                "Best IDE for Java",
                "https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_Ultimate.png",
                false,
                "admin");
        var actual = mvc.get().uri("/api/products/{code}", "intellij");
        assertThat(actual)
                .hasStatusOk()
                .bodyJson()
                .convertTo(ProductDto.class)
                .usingRecursiveComparison()
                .comparingOnlyFields("code", "name", "description", "imageUrl", "disabled", "createdBy")
                .isEqualTo(expected);
    }
}
