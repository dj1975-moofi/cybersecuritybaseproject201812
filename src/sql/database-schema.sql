/* Do not change this schema */
CREATE TABLE Users (
    id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(200),
    address varchar(200),
    email varchar(200) UNIQUE,
    password varchar(200)
);
