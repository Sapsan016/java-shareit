delete from bookings;
delete from ITEMS;
delete from USERS;
delete from requests;

ALTER TABLE comments ALTER COLUMN comment_id RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN booking_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN request_id RESTART WITH 1;
