insert into authority values (default,'ADMIN');
insert into authority values (default, 'VOLUNTEER');
insert into authority values (default,'USER');
insert into jwt_user_details (id, created_at, first_name, is_blocked, password, second_name, updated_at, username) values
(gen_random_uuid(),current_date, 'admin', false, '$2a$12$kq1SAcIy3vZuuW66MwetiuodXkXtz1Hihw9chjWD4u40IJV5zfsRW', 'adminko', current_date, 'admin@gmail.com');
insert into user_authorities values ((select id from jwt_user_details where username = 'admin@gmail.com'), (select id from authority where authority.authority = 'ADMIN'))
insert into cities values ('Sumy', 95.3, 50.909867, 34.802198)
insert into cities values ('Kharkiv', 95.3, 49, 34.802198);