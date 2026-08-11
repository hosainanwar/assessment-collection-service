package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.UserResponseDto;
import com.nhimex.assessment_collection.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto response = new UserResponseDto();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
