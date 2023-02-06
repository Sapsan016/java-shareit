package ru.practicum.shareit.request;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ItemRequestRepositoryTest {

    @Mock
    ItemRequestRepository requestRepository;

    User user = new User(1, "testUser1", "user1@email.com");
    Item item1 = new Item(1, "Item1", "Description1", true, 1L, null);
    Item item2 = new Item(2, "Item2", "Description2", true, 2L, 1L);

    ItemRequest request = new ItemRequest(1L, "Test request1", user, LocalDateTime.now());

    @Test
    public void getItemRequest() {
        ItemRequestRepository repository = Mockito.mock(ItemRequestRepository.class);
        Mockito.when(repository.findByRequesterIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(List.of(request));
        List<ItemRequest> list = repository.findByRequesterIdOrderByCreatedDesc(1L);
        Assertions.assertEquals(List.of(request), list);
    }


}