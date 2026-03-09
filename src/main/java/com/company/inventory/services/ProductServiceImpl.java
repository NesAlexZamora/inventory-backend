package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;

@Service
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ICategoryDao categoryDao;
    @Autowired
    private IProductDao productDao;


    @Override
    public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
        
        ProductResponseRest response = new ProductResponseRest();
        
        List<Product> list = new ArrayList<>();
        try {
            //buscaremos la categoria para setiarla al producto
            Optional<Category> category = categoryDao.findById(categoryId);

            if (category.isPresent()) {
                product.setCategory(category.get());
            }else{
                response.setMetadata("Respuesta no ok", "-1", "Categoria no encontrada asociada al producto");
                 return  new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
            }

            //SAVE THE PRODUCT
            Product productSaved = productDao.save(product);
            if(productSaved != null){
                list.add(productSaved);
                response.getProductResponse().setProducts(list);
                response.setMetadata("Respuesta ok", "00", "Producto guardado");
            }else{
                response.setMetadata("Respuesta no ok", "-1", "Categoria no encontrada");
                return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setMetadata("Respuesta no ok", "-1", "Error al guardar producto");
            e.getStackTrace();
            return  new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
