$schema=@'
create schema if not exists ChatBPTDb;
use ChatBPTDb;
drop table if exists ChatUser;
drop table if exists BannedUser;
drop table if exists Message;
drop table if exists ChatRoom;
drop table if exists PrivateChat;
drop table if exists Chat;
drop table if exists User;
create table User (username varchar(20) primary key,
                     fullName varchar(20), password varchar(25));
create table Chat (id int primary key auto_increment,
                    maxMessageCount int);
create table ChatRoom (id int, owner varchar(20),
                       name varchar(20),
                       foreign key (id) references Chat(id),
                       foreign key (owner) references User(username));
create table PrivateChat (id int,
                           user1 varchar(20), user2 varchar(20),
                           foreign key (id) references Chat(id),
                           foreign key (user1) references User(username),
                           foreign key (user2) references User(username));
create table Message (id int primary key auto_increment,
                       chatId int, username varchar(20),
                       message varchar(255), date timestamp,
                       foreign key (chatId) references Chat(id),
                       foreign key (username) references User(username));
create table BannedUser (username varchar(20), chatId int,
                          foreign key (username) references User(username),
                          foreign key (chatId) references ChatRoom(id));
create table ChatUser (username varchar(20), chatId int,
                        foreign key (username) references User(username),
                        foreign key (chatId) references Chat(id));
'@

echo $schema | docker exec -i mysql mysql
