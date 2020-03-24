INSERT INTO authorities (authority) VALUES ('user');
INSERT INTO authorities (authority) VALUES ('admin');
INSERT INTO users (email, namealasname, password, username, enabled, not_locker) VALUES ('correo@correo.es', 'Antonio', '$2y$12$pgO2JRJ8n1Q8Cw1sviyo/eQ9isg1zBGUyh3swEmMKEIXx3JUeOKpy', 'admin', true, true);
INSERT INTO users (email, namealasname, password, username, enabled, not_locker) VALUES ('correo@correo.com', 'Antonio', '$2y$12$pgO2JRJ8n1Q8Cw1sviyo/eQ9isg1zBGUyh3swEmMKEIXx3JUeOKpy', 'usuario', true, true);
insert into users_authorities (authority_id, user_id) values (1,1);
insert into users_authorities (authority_id, user_id) values (2,1);
insert into users_authorities (authority_id, user_id) values (1,2);
