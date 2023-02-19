package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired

    UserRepository userRepository;
    User user = new User(null, "John", "john.doe@mail.com");

    Item item = new Item(null, "Item1", "Item1 description", true, 1L, null);
    Item item2 = new Item(null, "ItemmM2", "Item1 description", true, 1L, null);


    @Test
    void searchUnAvailable() {
        userRepository.save(user);
        item.setAvailable(false);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Item> itemList = itemRepository.search("IteM");
        Assertions.assertEquals(2L, itemList.get(0).getId());
        Assertions.assertEquals(1, itemList.size());
    }

}