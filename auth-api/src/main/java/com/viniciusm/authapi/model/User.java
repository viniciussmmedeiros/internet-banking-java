package com.viniciusm.authapi.model;

import com.viniciusm.authapi.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "login", "appId" }) })
public class User {

    @Id
    private UUID id;

    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private UUID appId;

    private User(Builder builder) {
        this.id = builder.id;
        this.login = builder.login;
        this.password = builder.password;
        this.role = builder.role;
        this.appId = builder.appId;
    }

    public static class Builder {
        private UUID id;

        private String login;

        private String password;

        private UserRole role;

        private UUID appId;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder appId(UUID appId) {
            this.appId = appId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
