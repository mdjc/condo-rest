create table users (
	id int not null auto_increment primary key, 
	username varchar(30) unique,
	password char(60),
	role varchar(35)
);

create table condos (
	id int not null auto_increment primary key,
	name varchar(40) not null,
	manager int not null,
	balance double not null default 0,
	billing_day_of_month int,
	foreign	key (manager) references users(id)
);

create table apartments (
	id int not null auto_increment primary key,
	name varchar(20) not null,
	condo int not null,
	resident int,
	monthly_share double,
	foreign key(condo) references condos(id),
	foreign key(resident) references users(id),
	unique key(id, condo)	
);

create table bills (
	id int not null auto_increment primary key,
	apartment int not null,
	description varchar(100) not null,
	due_amount double not null,
	due_date date not null,
	payment_status varchar(30) not null,
	last_update_on date not null,
	payment_method varchar(20),
	proof_of_payment_extension varchar(3),	
	foreign key(apartment) references apartments(id)
);


create table outlays (
	id int not null auto_increment primary key,
	amount double not null,
	category varchar(30),
	created_on date not null,
	condo int not null,
	supplier varchar(40) default '',
	comment varchar(200) default '',
	receipt_image_extension varchar(3) not null,	
	foreign key(condo) references condos(id)
);