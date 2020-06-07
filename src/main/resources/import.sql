INSERT INTO authorities (authority) VALUES ('user');
INSERT INTO authorities (authority) VALUES ('admin');
INSERT INTO users (email, name, password, username, enabled, not_locker, quotes) VALUES ('correo@correo.es', 'Administrador', '$2y$12$pgO2JRJ8n1Q8Cw1sviyo/eQ9isg1zBGUyh3swEmMKEIXx3JUeOKpy', 'admin', true, true, 0);
insert into users_authorities (authority_id, user_id) values (1,1);
insert into users_authorities (authority_id, user_id) values (2,1);
insert into follows (user_id, followed_id) values (1, 1);
