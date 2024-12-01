package com.project.BlogGenie.services;

import com.project.BlogGenie.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

// It requires two things: the Blog entity and its primary key type, which is Integer.

//ProductsRepository
public interface BlogsRepository extends JpaRepository<Blog, Integer> {
}
