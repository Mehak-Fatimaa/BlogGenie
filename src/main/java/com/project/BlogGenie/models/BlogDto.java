package com.project.BlogGenie.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

// BlogDto
public class BlogDto {
    @NotEmpty(message = "The title is required")
    private String name; // Blog title

    @NotEmpty(message = "The author is required")
    private String author; // Blog author

    @NotEmpty(message = "The category is required")
    private String category; // Blog category

    @Size(min = 10, message = "The content should be at least 10 characters.")
    private String content; // Blog content (renamed from description)

    private MultipartFile imageFile; // Blog image

    // Getters and setters

    public @NotEmpty(message = "The title is required") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "The title is required") String name) {
        this.name = name;
    }

    public @NotEmpty(message = "The author is required") String getAuthor() {
        return author;
    }

    public void setAuthor(@NotEmpty(message = "The author is required") String author) {
        this.author = author;
    }

    public @NotEmpty(message = "The category is required") String getCategory() {
        return category;
    }

    public void setCategory(@NotEmpty(message = "The category is required") String category) {
        this.category = category;
    }

    public @Size(min = 10, message = "The content should be at least 10 characters.") String getContent() {
        return content;
    }

    public void setContent(@Size(min = 10, message = "The content should be at least 10 characters.") String content) {
        this.content = content;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
