INSERT INTO `roles` (`name`) VALUES
('ROLE_USER'),
('ROLE_MODERATOR'),
('ROLE_ADMIN');

INSERT INTO `users` (`email`, `password`, `username`) VALUES
('user@user.user', '$2a$10$zQtnRCqqjCFs6zFliZVe6OVvBRKyNtJuQCPABXzi0ty8mQhPePX9W', 'user'),
('moderator@moderator.moderator', '$2a$10$ebtrf2s2/xozi2luDcPQXuyiH9eJpzRf3.XMx/UFM3DjygrIMFMUm', 'moderator'),
('admin@admin.admin', '$2a$10$9j6PMpZBggWTlygEpKD/VehUWbifCsPSSBZuOOyxXQimz28EdIv8S', 'admin');

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
('1', '1'),
('2', '2'),
('3', '3');