package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ICategoryDao categoryDao;
    @Autowired
    private IProductDao productDao;

    // PARA GUARDAR
    @Override
    @Transactional
    public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
        ProductResponseRest response = new ProductResponseRest();

        List<Product> list = new ArrayList<>();
        try {
            // buscaremos la categoria para setiarla al producto
            Optional<Category> category = categoryDao.findById(categoryId);

            if (category.isPresent()) {
                product.setCategory(category.get());
            } else {
                response.setMetadata("Respuesta no ok", "-1", "Categoria no encontrada asociada al producto");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // SAVE THE PRODUCT
            Product productSaved = productDao.save(product);
            if (productSaved != null) {
                list.add(productSaved);
                response.getProductResponse().setProducts(list);
                response.setMetadata("Respuesta ok", "00", "Producto guardado");
            } else {
                response.setMetadata("Respuesta no ok", "-1", "Categoria no encontrada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setMetadata("Respuesta no ok", "-1", "Error al guardar producto");
            e.getStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Buscar por ID
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponseRest> searchById(Long id) {
        ProductResponseRest response = new ProductResponseRest();
        List<Product> list = new ArrayList<>();

        try {
            // search producto by id
            Optional<Product> product = productDao.findById(id);

            if (product.isPresent()) {

                byte[] imageDescompressed = Util.decompressZLib(product.get().getPicture());
                product.get().setPicture(imageDescompressed);
                list.add(product.get());
                response.getProductResponse().setProducts(list);
                response.setMetadata("Respuesta ok", "00", "Producto encontrado");

            } else {
                response.setMetadata("Respuesta no ok", "-1", "Producto no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response.setMetadata("Respuesta no ok", "-1", "Error al buscar producto");
            e.getStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponseRest> searchByName(String name) {
        ProductResponseRest response = new ProductResponseRest();
        List<Product> list = new ArrayList<>();
        List<Product> listAuxx = new ArrayList<>();

        try {
            // search producto by name
            listAuxx = productDao.findByNameContainingIgnoreCase(name);

            if (!listAuxx.isEmpty()) {
                listAuxx.stream().forEach((p) -> {
                    byte[] imageDescompressed = Util.decompressZLib(p.getPicture());
                    p.setPicture(imageDescompressed);
                    list.add(p);
                });

                response.getProductResponse().setProducts(list);
                response.setMetadata("Respuesta ok", "00", "Productoss encontrado");

            } else {
                response.setMetadata("Respuesta no ok", "-1", "Productos no encontrados ");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response.setMetadata("Respuesta no ok", "-1", "Error al buscar producto por nombre");
            e.getStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
