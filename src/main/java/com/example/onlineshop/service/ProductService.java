package com.example.onlineshop.service;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.exceptions.AlreadyExistException;
import com.example.onlineshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public void createProduct(ProductEntity product,String path) {
        CategoriesEntity category = categoryService.getCategoryByName(product.getCategories().getName(), path);
        product.setCategories(category);
        isValidProduct(product.getName(),path);
        productRepository.save(product);
    }

    public boolean isValidProduct(String product, String path) {
        if (productRepository.getByName(product).isEmpty()) {
            return true;
        }else {
            throw new AlreadyExistException("there is product with this name ." + path);
        }
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.getById(id);
    }

    public List<ProductEntity> getProductsByCategoryName(String categoryName) {
        Optional<CategoriesEntity> category = categoryService.getCategoryByNameBot(categoryName);
        return productRepository.getProductsByCategoryId(category.get().getId());
    }
    public void updateProductOnDeletingOrder(Long productId, int amount) {
        ProductEntity product = productRepository.getById(productId);
        product.setAmount(product.getAmount() + amount);
        productRepository.save(product);
    }

    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        Optional<ProductEntity> productEntity = productRepository.checkProductIsOrdered(id);
        if (productEntity.isPresent()) {
            throw new AlreadyExistException("this product is ordered by some user .product");
        }
        Optional<ProductEntity> product = productRepository.findById(id);
        product.get().setDeleted(true);
        productRepository.save(product.get());
    }

    public void updateProduct(ProductEntity product, Long oldProductId,String path) {
        ProductEntity oldProduct = getProductById(oldProductId);
        if (!Objects.equals(product.getName(), oldProduct.getName())) {
            isValidProduct(product.getName(),path);
        }
        product.setId(oldProduct.getId());
        product.setCreatedDate(oldProduct.getCreatedDate());
        productRepository.save(product);
    }

    public boolean updateProductOnBuyProduct(Long productId, int amount) {
        ProductEntity product = productRepository.getById(productId);
        if(product.getAmount()>=amount){
            product.setAmount(product.getAmount() - amount);
            productRepository.updateMY(product.getAmount(), product.getId());
            return true;
        }
        return false;
    }
    public List<ProductEntity> getAllProducts(Integer page, Model model) {
        PageRequest pageable = PageRequest.of(page,5);
        Page<ProductEntity> productEntities = productRepository.findAll(pageable);
        model.addAttribute("pages", productEntities.getTotalPages());
        return productEntities.getContent();
    }

}
