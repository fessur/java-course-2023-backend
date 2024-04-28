INSERT INTO link (url, last_check_time)
VALUES ('https://github.com/mshumer/gpt-investor', CURRENT_TIMESTAMP - INTERVAL '2 hour');

INSERT INTO link (url, last_check_time)
VALUES ('https://github.com/lewis-007/MediaCrawler', CURRENT_TIMESTAMP - INTERVAL '4 hour');

INSERT INTO link (url, last_check_time)
VALUES ('https://github.com/lichao-sun/Mora', CURRENT_TIMESTAMP - INTERVAL '5 hour');

INSERT INTO link (url, last_check_time)
VALUES ('https://github.com/jgthms/bulma', CURRENT_TIMESTAMP - INTERVAL '5 hour');

INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://github.com/mshumer/gpt-investor'));
INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://github.com/lewis-007/MediaCrawler'));
INSERT INTO chat_link (chat_id, link_id) VALUES (1, (SELECT id FROM link WHERE url = 'https://github.com/lichao-sun/Mora'));
