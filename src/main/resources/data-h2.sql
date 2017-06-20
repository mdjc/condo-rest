insert into users (username, password, role)  values('mirna', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('luis', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('manila', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('virgi', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('aldo', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('john', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('mary', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');

insert into buildings (name, manager, balance) values ('Shadai I', (select id from users where username='mirna' and role='MANAGER'), 100);
insert into buildings (name, manager, balance) values ('Loring  Place 2333', (select id from users where username='mirna' and role='MANAGER'), 50);
insert into buildings (name, manager, balance) values ('Mira Flores IV', (select id from users where username='luis' and role='MANAGER'), 220);

insert into apartments (name, building, resident) values ('1A', (select id from buildings where name = 'Shadai I'), (select id from users where username='virgi'));
insert into apartments (name, building, resident) values ('1B', (select id from buildings where name = 'Shadai I'), null);
insert into apartments (name, building, resident) values ('1C', (select id from buildings where name = 'Shadai I'), null);
insert into apartments (name, building, resident) values ('1D', (select id from buildings where name = 'Shadai I'), (select id from users where username='aldo'));


insert into apartments (name, building, resident) values ('1', (select id from buildings where name = 'Loring  Place 2333'), (select id from users where username='john'));
insert into apartments (name, building, resident) values ('2', (select id from buildings where name = 'Loring  Place 2333'), (select id from users where username='mary'));
insert into apartments (name, building, resident) values ('25F', (select id from buildings where name = 'Mira Flores IV'), (select id from users where username='mary'));