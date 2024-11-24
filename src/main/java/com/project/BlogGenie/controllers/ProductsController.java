package com.project.BlogGenie.controllers;

import com.project.BlogGenie.models.Product;
import com.project.BlogGenie.models.ProductDto;
import com.project.BlogGenie.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    //  isy service container se request krna hai islye autowired annotation use ki
    @Autowired
    private ProductsRepository repo;

//    method that reads products from database

    @GetMapping({"", "/"}) // ye url /products se accessible hai
//    ye method http get method se accessible hga islye ye annotation
    public String showProductList(Model model) {
//        List<Product> products = repo.findAll(); // in ascending
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id")); // in ascending
        model.addAttribute("products", products);
        return "products/index"; // name of html file which will return
    }

    //        this method will allow users to create new products
    @GetMapping("/create") // accessible from http get method
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";  // name of html file returned
    }

    //       this method will allow to save created products
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
//        checking the validations
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productsDto", "imageFile", "The image file is required."));
        }
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }
//        if no errors then save the form
//        save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

//        getting product dto info and storing it in product
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

//        saving product object in database
        repo.save(product);

//        move back to products list
        return "redirect:/products";
    }

    //   dislay the page that allows user to update product details
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";

    }

    //    to save edit details
    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id,
                                @Valid @ModelAttribute ProductDto productDto,
                                BindingResult result) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }
            if (!productDto.getImageFile().isEmpty()) {
                // delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception " + ex.getMessage());
                }
                //save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName((storageFileName));
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

//            updated wly ko database m save
            repo.save(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

//    delete products
    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id){
        try {
            Product product = repo.findById(id).get();

//            delete product image
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());

            try{
                Files.delete(imagePath);
            }
            catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            // delete the product
            repo.delete(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

}
