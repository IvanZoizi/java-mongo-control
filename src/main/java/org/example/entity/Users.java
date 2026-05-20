package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class Users {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    @Indexed
    private String role;

    private List<String> permissions;
}
