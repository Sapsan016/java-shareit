package ru.practicum.shareit.request;

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

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemRequestControllerTest {
    @Autowired
    @NonFinal
    ObjectMapper mapper;
    @MockBean
    @NonFinal
    ItemRequestServiceImpl requestService;
    @Autowired
    @NonFinal
    MockMvc mvc;
    static String HEADER = "X-Sharer-User-Id";
    static long ID = 1L;
    static LocalDateTime CREATED = LocalDateTime.now();
    User requester = new User(ID, "John", "john.doe@mail.com");
    ItemRequestAddDto requestAddDto = new ItemRequestAddDto("Request description", requester, CREATED);
    ItemRequest request = new ItemRequest(ID, "Request description", requester, CREATED);
    ItemRequest request2 = new ItemRequest(2L, "Request description", requester, CREATED);


    @Test
    void addNewRequest() throws Exception {
        when(requestService.addRequest(requestAddDto, ID))
                .thenReturn(request);
        String expectedResponse = mapper.writeValueAsString(request);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getRequest() throws Exception {
        when(requestService.getRequestById(ID, ID))
                .thenReturn(ItemRequestMapper.toItemRequestDto(request));
        String expectedResponse = mapper.writeValueAsString(request);
        mvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getRequestsWithParams() throws Exception {
        when(requestService.getAllRequestsWithParam(ID, 1L, 1L))
                .thenReturn(List.of(ItemRequestMapper.toItemRequestDto(request2)));
        String expectedResponse = mapper.writeValueAsString(List.of(request2));
        mvc.perform(get("/requests/all?from=1&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getUserRequest() throws Exception {
        when(requestService.getRequestsForUser(ID))
                .thenReturn(Stream.of(request, request2)
                        .map(ItemRequestMapper::toItemRequestDto)
                        .collect(Collectors.toList()));
        String expectedResponse = mapper.writeValueAsString(List.of(request, request2));
        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

}