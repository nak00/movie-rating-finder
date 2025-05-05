-- Only insert if the movie doesn't already exist
INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 1, 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 1994, 9.3, 91, 'https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDYwXkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 1);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 2, 'The Godfather', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 1972, 9.2, 97, 'https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 2);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 3, 'The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 2008, 9.0, 94, 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 3);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 4, 'The Godfather Part II', 'The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.', 1974, 9.0, 96, 'https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 4);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 5, '12 Angry Men', 'A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.', 1957, 9.0, 100, 'https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 5);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 6, 'Schindler''s List', 'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.', 1993, 9.0, 98, 'https://m.media-amazon.com/images/M/MV5BNDE4OTMxMTctNmRhYy00NWE2LTg3YzItYTk3M2UwOTU5Njg4XkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 6);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 7, 'The Lord of the Rings: The Return of the King', 'Gandalf and Aragorn lead the World of Men against Sauron''s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.', 2003, 9.0, 93, 'https://m.media-amazon.com/images/M/MV5BNzA5ZDNlZWMtM2NhNS00NDJjLTk4NDItYTRmY2EwMWZlMTY3XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 7);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 8, 'Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 1994, 8.9, 92, 'https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 8);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 9, 'The Lord of the Rings: The Fellowship of the Ring', 'A meek Hobbit from the Shire and eight companions set out on a journey to destroy the powerful One Ring and save Middle-earth from the Dark Lord Sauron.', 2001, 8.8, 91, 'https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 9);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 10, 'The Good, the Bad and the Ugly', 'A bounty hunting scam joins two men in an uneasy alliance against a third in a race to find a fortune in gold buried in a remote cemetery.', 1966, 8.8, 97, 'https://m.media-amazon.com/images/M/MV5BNjJlYmNkZGItM2NhYy00MjlmLTk5NmQtNjg1NmM2ODU4OTMwXkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 10);

-- Add 10 more movies
INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 11, 'Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.', 1994, 8.8, 71, 'https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 11);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 12, 'Fight Club', 'An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.', 1999, 8.8, 79, 'https://m.media-amazon.com/images/M/MV5BMmEzNTkxYjQtZTc0MC00YTVjLTg5ZTEtZWMwOWVlYzY0NWIwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 12);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 13, 'Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 2010, 8.8, 87, 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 13);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 14, 'The Lord of the Rings: The Two Towers', 'While Frodo and Sam edge closer to Mordor with the help of the shifty Gollum, the divided fellowship makes a stand against Sauron''s new ally, Saruman, and his hordes of Isengard.', 2002, 8.8, 95, 'https://m.media-amazon.com/images/M/MV5BZGMxZTdjZmYtMmE2Ni00ZTdkLWI5NTgtNjlmMjBiNzU2MmI5XkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 14);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 15, 'Star Wars: Episode V - The Empire Strikes Back', 'After the Rebels are brutally overpowered by the Empire on the ice planet Hoth, Luke Skywalker begins Jedi training with Yoda, while his friends are pursued by Darth Vader and a bounty hunter named Boba Fett.', 1980, 8.7, 94, 'https://m.media-amazon.com/images/M/MV5BYmU1NDRjNDgtMzhiMi00NjZmLTg5NGItZDNiZjU5NTU4OTE0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 15);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 16, 'The Matrix', 'When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.', 1999, 8.7, 88, 'https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 16);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 17, 'Goodfellas', 'The story of Henry Hill and his life in the mob, covering his relationship with his wife Karen Hill and his mob partners Jimmy Conway and Tommy DeVito in the Italian-American crime syndicate.', 1990, 8.7, 96, 'https://m.media-amazon.com/images/M/MV5BY2NkZjEzMDgtN2RjYy00YzM1LWI4ZmQtMjIwYjFjNmI3ZGEwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 17);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 18, 'One Flew Over the Cuckoo''s Nest', 'A criminal pleads insanity and is admitted to a mental institution, where he rebels against the oppressive nurse and rallies up the scared patients.', 1975, 8.7, 94, 'https://m.media-amazon.com/images/M/MV5BZjA0OWVhOTAtYWQxNi00YzNhLWI4ZjYtNjFjZTEyYjJlNDVlL2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 18);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 19, 'Se7en', 'Two detectives, a rookie and a veteran, hunt a serial killer who uses the seven deadly sins as his motives.', 1995, 8.6, 82, 'https://m.media-amazon.com/images/M/MV5BOTUwODM5MTctZjczMi00OTk4LTg3NWUtNmVhMTAzNTNjYjcyXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 19);

INSERT INTO movies (id, name, description, release_year, imdb_rating, rotten_tomatoes_rating, poster_url)
SELECT 20, 'The Silence of the Lambs', 'A young F.B.I. cadet must receive the help of an incarcerated and manipulative cannibal killer to help catch another serial killer, a madman who skins his victims.', 1991, 8.6, 95, 'https://m.media-amazon.com/images/M/MV5BNjNhZTk0ZmEtNjJhMi00YzFlLWE1MmEtYzM1M2ZmMGMwMTU4XkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 20);
