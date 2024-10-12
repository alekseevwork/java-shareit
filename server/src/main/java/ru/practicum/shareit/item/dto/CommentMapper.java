package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> toDto(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    public static Comment toComment(CommentDto dto) {
        if (dto == null) {
            return null;
        }
        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(dto.getCreated())
                .build();
    }
}
