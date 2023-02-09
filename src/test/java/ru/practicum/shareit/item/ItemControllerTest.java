package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemControllerTest {
    @Autowired
    @NonFinal
    ObjectMapper mapper;
    @MockBean
    @NonFinal
    ItemServiceImpl itemService;
    @Autowired
    @NonFinal
    MockMvc mvc;
    static String HEADER = "X-Sharer-User-Id";
    static long ID = 1L;

    User owner = new User(ID, "John", "john.doe@mail.com");
    Item item1 = new Item(ID, "Item1", "Item1 description", true, ID,
            null);
    Item item2 = new Item(2L, "Item2", "Item2 description", true, ID,
            null);
    Item item1updated = new Item(ID, "Item1update", "Item1 description update",
            false, ID, null);
    ItemAddDto itemAddDto1 = new ItemAddDto("Item1", "Item1 description", true,
            null);
    ItemAddDto itemAddDto1update = new ItemAddDto("Item1update", "Item1 description update",
            false, null);
    ItemDto itemDto1 = new ItemDto(ID, "Item1", "Item1 description", true, ID,
            null, null, null, null);
    ItemDto itemDto2 = new ItemDto(2L, "Item2", "Item2 description", true, ID,
            null, null, null, null);
    CommentAddDto commentAddDto = new CommentAddDto("Text comment", 0, 0, null);
    Comment comment = new Comment(ID, "Text comment", ID, owner, LocalDateTime.now());

    @Test
    void addNewItem() throws Exception {
        when(itemService.addItem(itemAddDto1, ID))
                .thenReturn(item1);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemAddDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.owner", is(item1.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(item1.getRequestId())));
    }

    @Test
    void addNewComment() throws Exception {
        when(itemService.addComment(commentAddDto, ID, ID))
                .thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(comment.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(comment.getAuthor().getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())));
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.getItems(ID))
                .thenReturn(List.of(itemDto1, itemDto2));
        mvc.perform(
                        get("/items")
                                .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(itemDto1, itemDto2))));
    }

    @Test
    void getAllItemsWithParam() throws Exception {
        when(itemService.getItemsWithParam(ID, 1L, 1L))
                .thenReturn(List.of(itemDto2));
        mvc.perform(
                        get("/items?from=1&size=1")
                                .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(itemDto2))));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(ID, ID))
                .thenReturn(itemDto1);
        mvc.perform(get("/items/1")
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto1.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto1.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDto1.getComments())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(ID, ID, itemAddDto1update))
                .thenReturn(item1updated);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemAddDto1update))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(jsonPath("$.description", is(item1updated.getDescription())))
                .andExpect(jsonPath("$.available", is(item1updated.getAvailable())))
                .andExpect(jsonPath("$.owner", is(item1updated.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(item1updated.getRequestId())));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems("item"))
                .thenReturn(List.of(item1, item2));
        mvc.perform(
                        get("/items/search?text=item")
                                .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(
                        ItemMapper.toItemDto(item1),
                        ItemMapper.toItemDto(item2)))));
    }

    @Test
    void searchItemsWithParams() throws Exception {
        when(itemService.searchItemsWithParams("item", 1L, 1L))
                .thenReturn(List.of(item2));
        mvc.perform(
                        get("/items/search?text=item&from=1&size=1")
                                .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(
                        ItemMapper.toItemDto(item2)))));
    }
}