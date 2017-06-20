create table users (
	id int not null auto_increment primary key, 
	username varchar(30) unique,
	password char(60),
	role varchar(35)
);

create table buildings (
	id int not null auto_increment primary key,
	name varchar(40) not null,
	manager int not null,
	balance double not null default 0,
	foreign	key (manager) references users(id)
);

create table apartments(
	id int not null auto_increment primary key,
	name varchar(20) not null,
	building int not null,
	resident int,
	foreign key(building) references buildings(id),
	foreign key(resident) references users(id)	
);



