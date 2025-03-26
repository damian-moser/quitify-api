package ch.gibb.quitify.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "change_password", nullable = false)
    private Boolean changePassword;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "fk_user_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Set<String> getRoleNames() {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
