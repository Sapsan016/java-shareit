package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;


public class CommentMapper {
    public static CommentDTO toCommentDto(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentAddDto commentAddDto) {
        return new Comment(
                0,
                commentAddDto.getText(),
                0,
                null,
                null
        );
    }
}
