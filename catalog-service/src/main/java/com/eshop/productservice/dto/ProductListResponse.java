package com.eshop.productservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponse {

    private List<ProductResponse> products;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;

}
