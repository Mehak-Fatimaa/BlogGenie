package com.project.BlogGenie.controllers;

import com.project.BlogGenie.models.Blog;
import com.project.BlogGenie.models.BlogDto;
import com.project.BlogGenie.services.BlogsRepository;
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
@RequestMapping("/blogs")
public class BlogsController {

    @Autowired
    private BlogsRepository repo;

    @GetMapping({"", "/"})
    public String showBlogList(Model model) {
        List<Blog> blogs = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("blogs", blogs);
        return "blogs/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        BlogDto blogDto = new BlogDto();
        model.addAttribute("blogDto", blogDto);
        return "blogs/CreateBlog";
    }

    @PostMapping("/create")
    public String createBlog(@Valid @ModelAttribute BlogDto blogDto, BindingResult result) {
        if (blogDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("blogDto", "imageFile", "The image file is required."));
        }
        if (result.hasErrors()) {
            return "/blogs/CreateBlog";
        }

        MultipartFile image = blogDto.getImageFile();
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

        Blog blog = new Blog();
        blog.setName(blogDto.getName());
        blog.setAuthor(blogDto.getAuthor());
        blog.setCategory(blogDto.getCategory());
        blog.setContent(blogDto.getContent()); // Updated
        blog.setCreatedAt(createdAt);
        blog.setImageFileName(storageFileName);

        repo.save(blog);

        return "redirect:/blogs";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Blog blog = repo.findById(id).get();
            model.addAttribute("blog", blog);

            BlogDto blogDto = new BlogDto();
            blogDto.setName(blog.getName());
            blogDto.setAuthor(blog.getAuthor());
            blogDto.setCategory(blog.getCategory());
            blogDto.setContent(blog.getContent()); // Updated

            model.addAttribute("blogDto", blogDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/blogs";
        }
        return "blogs/EditBlog";
    }

    @PostMapping("/edit")
    public String updateBlog(Model model, @RequestParam int id,
                             @Valid @ModelAttribute BlogDto blogDto,
                             BindingResult result) {
        try {
            Blog blog = repo.findById(id).get();
            model.addAttribute("blog", blog);

            if (result.hasErrors()) {
                return "blogs/EditBlog";
            }
            if (!blogDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + blog.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                MultipartFile image = blogDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                blog.setImageFileName(storageFileName);
            }

            blog.setName(blogDto.getName());
            blog.setAuthor(blogDto.getAuthor());
            blog.setCategory(blogDto.getCategory());
            blog.setContent(blogDto.getContent()); // Updated

            repo.save(blog);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/blogs";
    }

    @GetMapping("/delete")
    public String deleteBlog(@RequestParam int id) {
        try {
            Blog blog = repo.findById(id).get();

            Path imagePath = Paths.get("public/images/" + blog.getImageFileName());
            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            repo.delete(blog);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/blogs";
    }

    @GetMapping("/view")
    public String viewBlog(@RequestParam int id, Model model) {
        try {
            Blog blog = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid blog ID: " + id));
            model.addAttribute("blog", blog);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/blogs";
        }
        return "blogs/ViewBlog";
    }
}
