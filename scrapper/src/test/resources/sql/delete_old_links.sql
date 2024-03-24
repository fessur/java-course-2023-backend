DELETE FROM chat_link WHERE link_id in
    (SELECT id FROM link WHERE url IN (
       'https://github.com/mshumer/gpt-investor',
       'https://github.com/lewis-007/MediaCrawler',
       'https://github.com/lichao-sun/Mora',
       'https://github.com/jgthms/bulma'
    )
);

DELETE FROM link WHERE url IN (
   'https://github.com/mshumer/gpt-investor',
   'https://github.com/lewis-007/MediaCrawler',
   'https://github.com/lichao-sun/Mora',
   'https://github.com/jgthms/bulma'
);
