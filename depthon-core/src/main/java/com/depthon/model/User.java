package com.depthon.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Subdivision subdivision;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_followed_subdivisions",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "subdivision")
    private Set<Subdivision> followedSubdivisions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        USER, ADMIN, MODERATOR
    }
}