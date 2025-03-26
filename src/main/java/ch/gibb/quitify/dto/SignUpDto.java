package ch.gibb.quitify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    private String username;
    private String displayName;
    private String password;
}
