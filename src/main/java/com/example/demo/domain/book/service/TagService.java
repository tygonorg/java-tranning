package com.example.demo.domain.book.service;

import com.example.demo.domain.book.dto.TagDTO;
import com.example.demo.domain.book.entity.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(TagDTO tagDTO);

    Tag updateTag(Long id, TagDTO tagDTO);

    void deleteTag(Long id);

    List<Tag> getAllTags();

    Tag getTagById(Long id);
}
