package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;


public class CommentMapper {
    public static CommentDTO toCommentDto(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getCreated()
        );
    }
}
