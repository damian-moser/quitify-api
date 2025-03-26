package ch.gibb.quitify.dto;

import java.sql.Timestamp;
import java.util.Set;

import ch.gibb.quitify.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    public UserDto(User user) {
        this.id = user.getId();
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
        this.changePassword = user.getChangePassword();
        this.createdAt = user.getCreatedAt();
        this.isPublic = user.getIsPublic();
        this.roles = user.getRoleNames();
    }

    private Long id;
    private String displayName;
    private String username;
    private boolean changePassword;
    private Timestamp createdAt;
    private boolean isPublic;
    private Set<String> roles;
}
