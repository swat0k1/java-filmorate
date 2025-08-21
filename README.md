# java-filmorate
Repository of Filmorate project.
![DB structure scheme.](https://i.ibb.co/XkbXR24H/db-scheme.png)

Query examples:
1) Get all users
select *
from users;

2) Get all friends of user
SELECT *
FROM users
WHERE id IN (
	SELECT friend_id
	FROM friendships
	WHERE user_id = sample_id
	AND status = 'CONFIRMED');
	
3) Get name of all films that user liked
SELECT name
FROM films
WHERE id IN (
	SELECT film_id
	FROM likes
	WHERE user_id = sample_id);
	
4) Get all films
SELECT *
FROM films;

5) Get all COMEDY films
SELECT *
FROM films
WHERE id IN (
	SELECT film_id
	FROM genres
	WHERE genre = 'COMEDY');
