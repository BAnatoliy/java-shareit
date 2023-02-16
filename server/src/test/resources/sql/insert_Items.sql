insert into users (name, email) values ('user1', 'user1@ya.ru');
insert into users (name, email) values ('user2', 'user2@ya.ru');
insert into users (name, email) values ('user3', 'user3@ya.ru');
insert into users (name, email) values ('user4', 'user4@ya.ru');
insert into users (name, email) values ('user5', 'user5@ya.ru');

insert into items (name, description, available, user_id) values ('item1', 'item description1', true, 1);
insert into items (name, description, available, user_id) values ('item2', 'item description2', true, 1);
insert into items (name, description, available, user_id) values ('item3', 'item description3', false, 1);
insert into items (name, description, available, user_id) values ('item4', 'item description4', true, 1);
insert into items (name, description, available, user_id) values ('item5', 'item description5', true, 2);
insert into items (name, description, available, user_id) values ('item6', 'item description6', true, 2);
insert into items (name, description, available, user_id) values ('item7', 'item description7', true, 2);
insert into items (name, description, available, user_id) values ('item8', 'item description8', false, 3);
insert into items (name, description, available, user_id) values ('item9', 'item description9', false, 3);
insert into items (name, description, available, user_id) values ('item10', 'item description10', true, 4);

insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp - interval '7' day, current_timestamp - interval '5' day, 'APPROVED', 5, 1);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp - interval '3' day, current_timestamp - interval '1' day, 'REJECTED', 5, 5);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp + interval '1' day, current_timestamp + interval '2' day, 'WAITING', 5, 7);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp - interval '2' day, current_timestamp - interval '1' day, 'APPROVED', 4, 1);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp + interval '3' day, current_timestamp + interval '5' day, 'APPROVED', 4, 9);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp + interval '4' day, current_timestamp + interval '7' day, 'APPROVED', 4, 5);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp - interval '3' day, current_timestamp + interval '5' day, 'APPROVED', 3, 10);
insert into bookings (start_booking, end_booking, status, user_id, item_id)
values (current_timestamp - interval '1' hour, current_timestamp + interval '1' hour, 'APPROVED', 3, 2);

insert into comments (text, created, user_id, item_id) values ('text1', current_timestamp - interval '1' day, 5, 1);
insert into comments (text, created, user_id, item_id) values ('text2', current_timestamp - interval '5' hour, 5, 5);
insert into comments (text, created, user_id, item_id) values ('text3', current_timestamp - interval '45' minute, 4, 1);

insert into requests (description, created, user_id)
values ('request`s description 1', current_timestamp - interval '7' day, 3);
insert into requests (description, created, user_id)
values ('request`s description 2', current_timestamp - interval '5' day , 4);
insert into requests (description, created, user_id)
values ('request`s description 3', current_timestamp - interval '3' day, 4);
insert into requests (description, created, user_id)
values ('request`s description 4', current_timestamp - interval '7' hour, 5);
insert into requests (description, created, user_id)
values ('request`s description 5', current_timestamp - interval '10' minute, 5);

insert into items (name, description, available, user_id, request_id)
values ('item11', 'item description11', true, 5, 1);
insert into items (name, description, available, user_id, request_id)
values ('item12', 'item description12', true, 5, 2);
