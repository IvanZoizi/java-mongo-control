package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class AuthDTO {
    @NonNull
    private String login;
    @NonNull
    private String password;
}
