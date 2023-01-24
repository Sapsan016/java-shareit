package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;



@Component
public interface UserRepository extends JpaRepository<User, Long> {
//    @Modifying
//    @Query("update User u set u.phone = :phone where u.id = :id")
//    User update(@Param(value = "id") long id, @Param(value = "phone") String phone);
}

//    UserDto getUserById(long id);
//
//    List<UserDto> getAllUsers();
//
//    UserDto createUser(User user);
//
//    UserDto updateUser(long userId, User user);
//
//    void deleteUserById(long id);
