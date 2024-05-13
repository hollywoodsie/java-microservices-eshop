//package com.eshop.productservice.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.eshop.productservice.dto.ProductListResponse;
//import com.eshop.productservice.dto.ProductRequest;
//import com.eshop.productservice.dto.ProductResponse;
//import com.eshop.productservice.exception.NotFoundException;
//import com.eshop.productservice.model.Product;
//import com.eshop.productservice.repository.ProductRepository;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.math.BigDecimal;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @InjectMocks
//    private ProductService productService;
//
//    @Test
//    public void testCreateProduct_Success() {
//
//        ProductRequest productRequest = new ProductRequest("Test Product", "Description", new BigDecimal(10));
//        Product product = Product.builder().name("Test Product").description("Description").price(new BigDecimal(10)).build();
//
//
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        ProductResponse response = productService.createProduct(productRequest);
//
//
//        assertNotNull(response);
//        assertEquals("Test Product", response.getName());
//        assertEquals("Description", response.getDescription());
//        assertEquals(new BigDecimal(10), response.getPrice());
//    }
//
//    @Test
//    public void testGetAllProducts_Success() {
//
//        List<Product> products = List.of(
//                Product.builder().name("Product1").description("Description1").price(new BigDecimal(10)).build(),
//                Product.builder().name("Product2").description("Description2").price(new BigDecimal(20)).build()
//        );
//        Page<Product> productPage = new PageImpl<>(products);
//        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
//
//
//        ProductListResponse response = productService.getAllProducts(0, 10);
//
//
//        assertNotNull(response);
//        assertEquals(2, response.getTotalItems());
//        assertEquals(0, response.getCurrentPage());
//        assertEquals(1, response.getTotalPages());
//        assertEquals(2, response.getProducts().size());
//    }
//
//    @Test
//    public void testGetOneProduct_Success() {
//
//        Long productId = 1L;
//        Product product = Product.builder().name("Test Product").description("Description").price(new BigDecimal(10)).build();
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//
//
//        ProductResponse response = productService.getOneProduct(productId);
//
//
//        assertNotNull(response);
//        assertEquals("Test Product", response.getName());
//        assertEquals("Description", response.getDescription());
//        assertEquals(new BigDecimal(10), response.getPrice());
//    }
//
//    @Test
//    public void testCreateProduct_Failure() {
//
//        ProductRequest productRequest = new ProductRequest("Test Product", "Description", new BigDecimal(10));
//        when(productRepository.save(any(Product.class))).thenThrow(RuntimeException.class);
//
//
//        assertThrows(RuntimeException.class, () -> {
//            productService.createProduct(productRequest);
//        });
//    }
//
//    @Test
//    public void testGetOneProduct_ProductNotFound() {
//
//        Long productId = 1L;
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> {
//            productService.getOneProduct(productId);
//        });
//    }
//
//}
