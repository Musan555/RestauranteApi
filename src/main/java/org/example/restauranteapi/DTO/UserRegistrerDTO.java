package org.example.restauranteapi.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrerDTO {
    private String username;
    private String email;
    private String password;
    private String password2;

}
