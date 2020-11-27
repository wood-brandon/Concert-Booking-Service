package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.User;

public class UserMapper {

    public static User toDomainModel(UserDTO userDTO){
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        return user;
    }

    public static UserDTO toDto(User user){
        UserDTO userDTO = new UserDTO(user.getUsername(),user.getPassword());
        return userDTO;
    }
}
