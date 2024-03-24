INSERT INTO link (url) VALUES ('https://stackoverflow.com/questions/927358');
INSERT INTO link (url) VALUES ('https://stackoverflow.com/questions/2003505');
INSERT INTO link (url) VALUES ('https://stackoverflow.com/questions/292357');
INSERT INTO link (url) VALUES ('https://stackoverflow.com/questions/477816');
INSERT INTO link (url) VALUES ('https://stackoverflow.com/questions/348170');
INSERT INTO link (url) VALUES ('https://github.com/spring-projects/spring-framework');
INSERT INTO link (url) VALUES ('https://github.com/hibernate/hibernate-orm');

INSERT INTO chat (id) VALUES (1);
INSERT INTO chat (id) VALUES (2);
INSERT INTO chat (id) VALUES (3);
INSERT INTO chat (id) VALUES (4);
INSERT INTO chat (id) VALUES (5);

INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/927358'));
INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/2003505'));
INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/292357'));

INSERT INTO chat_link (chat_id, link_id) VALUES (2, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/2003505'));
INSERT INTO chat_link (chat_id, link_id) VALUES (2, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/292357'));
INSERT INTO chat_link (chat_id, link_id) VALUES (2, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/477816'));

INSERT INTO chat_link (chat_id, link_id) VALUES (3, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/927358'));
INSERT INTO chat_link (chat_id, link_id) VALUES (3, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/477816'));
INSERT INTO chat_link (chat_id, link_id) VALUES (3, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/348170'));
INSERT INTO chat_link (chat_id, link_id) VALUES (3, (SELECT id FROM link WHERE url = 'https://github.com/spring-projects/spring-framework'));

INSERT INTO chat_link (chat_id, link_id) VALUES (4, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/2003505'));
INSERT INTO chat_link (chat_id, link_id) VALUES (4, (SELECT id FROM link WHERE url = 'https://stackoverflow.com/questions/477816'));
INSERT INTO chat_link (chat_id, link_id) VALUES (4, (SELECT id FROM link WHERE url = 'https://github.com/hibernate/hibernate-orm'));
