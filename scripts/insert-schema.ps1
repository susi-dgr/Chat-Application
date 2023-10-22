$schema=@'
use ChatBPTDb;
insert into User values ('max', 'Max Mustermann', 'max');
insert into User values ('ela', 'Ela Mustermann', 'ela');

insert into Chat values (1, 50);
insert into ChatRoom values (1, 'max', 'Chat1');
insert into Chat values (2, 50);
insert into PrivateChat values (2, 'max', 'ela');

insert into ChatUser values ('max', 1);
insert into ChatUser values ('max', 2);
insert into ChatUser values ('ela', 2);

insert into Message values (1, 1, 'max', 'Hallo Ela', '2023-01-01 00:00:00');
insert into Message values (2, 1, 'ela', 'Hallo Max', '2023-01-02 00:00:00');
insert into Message values (3, 1, 'max', 'Wie gehts?', '2023-01-03 00:00:00');
insert into Message values (4, 1, 'ela', 'Gut und dir?', '2023-01-04 00:00:00');
insert into Message values (5, 1, 'max', 'Auch gut', '2023-01-05 00:00:00');
'@

echo $schema | docker exec -i mysql mysql