package com.codX.pos.service;

import com.codX.pos.dto.ProductDTO;

public interface ProductService {
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    ProductDTO addProduct(ProductDTO productDTO);
}