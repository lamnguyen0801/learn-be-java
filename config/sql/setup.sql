CREATE DATABASE java_sql CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE USER 'java_sql'@'localhost' IDENTIFIED BY 'java@vietdefi';
GRANT ALL ON java_sql.* TO 'java_sql'@'localhost';
CREATE USER 'java_sql'@'%' IDENTIFIED BY 'java@vietdefi';
GRANT ALL ON java_sql.* TO 'java_sql'@'%';