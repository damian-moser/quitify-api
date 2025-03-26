CREATE DATABASE IF NOT EXISTS `quitify` DEFAULT CHARACTER
SET
    utf8mb4 COLLATE utf8mb4_general_ci;

USE `quitify`;

CREATE TABLE
    `roles` (
                `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                `name` VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE
    `users` (
                `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR(255) NOT NULL UNIQUE,
                `password` VARCHAR(255) NOT NULL,
                `change_password` TINYINT (1) NOT NULL DEFAULT 0,
                `display_name` VARCHAR(255) NOT NULL UNIQUE,
                `is_public` TINYINT (1) NOT NULL DEFAULT 0,
                `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE
    `users_roles` (
                      `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                      `fk_user_id` BIGINT NOT NULL,
                      `fk_role_id` BIGINT NOT NULL,
                      FOREIGN KEY (`fk_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                      FOREIGN KEY (`fk_role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                      UNIQUE (`fk_user_id`, `fk_role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE
    `tokens` (
                 `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                 `token` VARCHAR(255) NOT NULL UNIQUE,
                 `is_logged_out` TINYINT (1) NOT NULL DEFAULT 0,
                 `fk_user_id` BIGINT NOT NULL,
                 FOREIGN KEY (`fk_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_user_roles_fk_user_id ON `users_roles` (`fk_user_id`);

CREATE INDEX idx_user_roles_fk_role_id ON `users_roles` (`fk_role_id`);

CREATE INDEX idx_tokens_fk_user_id ON `tokens` (`fk_user_id`);

INSERT INTO
    `roles` (`name`)
VALUES
    ('ADMIN'),
    ('USER');