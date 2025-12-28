package com.example.demo.domain.book.service.impl;

import com.example.demo.domain.book.dto.TagDTO;
import com.example.demo.domain.book.entity.Tag;
import com.example.demo.domain.book.repository.TagRepository;
import com.example.demo.domain.book.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Tag createTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.domain.shared.exception.ResourceNotFoundException(
                        "Tag not found with id: " + id));
        tag.setName(tagDTO.getName());
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }
}
