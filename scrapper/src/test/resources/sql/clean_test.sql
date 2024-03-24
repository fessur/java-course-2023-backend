DELETE FROM chat_link WHERE chat_id IN (1, 2, 3, 4, 5);

DELETE FROM chat WHERE id IN (1, 2, 3, 4, 5);

DELETE FROM link WHERE url IN (
   'https://stackoverflow.com/questions/927358',
   'https://stackoverflow.com/questions/2003505',
   'https://stackoverflow.com/questions/292357',
   'https://stackoverflow.com/questions/477816',
   'https://stackoverflow.com/questions/348170',
   'https://github.com/spring-projects/spring-framework',
   'https://github.com/hibernate/hibernate-orm'
);
