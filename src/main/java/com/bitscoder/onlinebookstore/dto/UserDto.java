package com.bitscoder.onlinebookstore.dto;

import com.bitscoder.onlinebookstore.constant.Roles;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String name;
    private String email;
    private String password;
    private Roles roles;
    private List<Long> bookIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String name;
        private String email;
        private Roles roles;
        private List<String> bookIds;
    }
}
