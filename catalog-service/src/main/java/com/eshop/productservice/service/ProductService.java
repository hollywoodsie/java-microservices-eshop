package com.eshop.productservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eshop.productservice.dto.ProductListResponse;
import com.eshop.productservice.dto.ProductRequest;
import com.eshop.productservice.dto.ProductResponse;
import com.eshop.productservice.messaging.ProductDeletedEvent;
import com.eshop.productservice.model.Product;
import com.eshop.productservice.repository.ProductRepository;
import com.eshop.productservice.messaging.RabbitMQPublisher;
import jakarta.validation.Valid;
import com.eshop.productservice.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitMQPublisher rabbitMQPublisher;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        return convertToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductListResponse getAllProducts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productDTOs = products.stream()
                .map(this::convertToProductResponse)
                .toList();

        Integer totalPages = products.getTotalPages();
        Integer currentPage = products.getNumber();
        Integer totalItems = (int)products.getTotalElements();

        return ProductListResponse.builder().products(productDTOs).totalItems(totalItems).currentPage(currentPage).totalPages(totalPages).build();
    }

    @Transactional(readOnly = true)
    public ProductResponse getOneProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));
        return convertToProductResponse(product);
    }

    public ProductResponse updateProduct(Long productId, @Valid ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        return convertToProductResponse(product);
    }

    public void deleteProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            productRepository.deleteById(productId);
            rabbitMQPublisher.publishProductDeletedMessage(new ProductDeletedEvent(productId));
        } else {
            throw new NotFoundException("Product with ID " + productId + " not found");
        }
    }

    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
