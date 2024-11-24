package com.project.BlogGenie.controllers;

import com.project.BlogGenie.models.Product;
import com.project.BlogGenie.models.ProductDto;
import com.project.BlogGenie.services.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String showCreatePage(Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";  // name of html file returned
    }
}
