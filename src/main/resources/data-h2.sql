insert into users (username, password, role)  values('mirna', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('luis', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('manila', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'MANAGER');
insert into users (username, password, role)  values('virgi', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('aldo', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('john', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');
insert into users (username, password, role)  values('mary', '$2a$10$nZAnnVBnl0r23Iii2FBUPOJNcCFL9x9MakFDdlzw2vbN5tUeWcHui', 'RESIDENT');

insert into condos (name, manager, balance, billing_day_of_month) values ('Shadai I', (select id from users where username='mirna' and role='MANAGER'), 100, 15);
insert into condos (name, manager, balance, billing_day_of_month) values ('Loring  Place 2333', (select id from users where username='mirna' and role='MANAGER'), 50, 1);
insert into condos (name, manager, balance, billing_day_of_month) values ('Mira Flores IV', (select id from users where username='luis' and role='MANAGER'), 220, 1);
insert into condos (name, manager, balance, billing_day_of_month) values('Baldwing IV', (select id from users where username='luis' and role='MANAGER'), 0, 1);

insert into apartments (name, condo, resident, monthly_share) values ('1A', (select id from condos where name = 'Shadai I'), (select id from users where username='virgi'), 10);
insert into apartments (name, condo, resident, monthly_share) values ('1B', (select id from condos where name = 'Shadai I'), null, 10);
insert into apartments (name, condo, resident, monthly_share) values ('1C', (select id from condos where name = 'Shadai I'), null, 15);
insert into apartments (name, condo, resident, monthly_share) values ('1D', (select id from condos where name = 'Shadai I'), (select id from users where username='aldo'), 15);

insert into apartments (name, condo, resident, monthly_share) values ('1', (select id from condos where name = 'Loring  Place 2333'), (select id from users where username='john'), 600);
insert into apartments (name, condo, resident, monthly_share) values ('2', (select id from condos where name = 'Loring  Place 2333'), (select id from users where username='mary'), 300);
insert into apartments (name, condo, resident, monthly_share) values ('25F', (select id from condos where name = 'Mira Flores IV'), (select id from users where username='mary'), 300);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1'), 'cuota mensual',  '20', '2017-6-15', 'PAID_CONFIRMED', '2017-6-15', 'CHECK', 'JPG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method)
values((select id from apartments where name='1'), 'cuota mensual',  5.55, '2017-6-15',  'PENDING', '2017-6-15', null);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1D'), 'cuota mensual',  '20', '2017-6-15', 'PAID_CONFIRMED', '2017-6-15', 'CHECK', 'JPG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method)
values((select id from apartments where name='1A'), 'cuota mensual',  5.55, '2017-6-15',  'PENDING', '2017-6-15', null);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method)
values((select id from apartments where name='2'), 'cuota mensual',  10, '2017-5-15', 'PENDING', '2017-5-15', null);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method)
values((select id from apartments where name='1A'), 'cuota mensual',  10, '2017-6-15', 'PENDING', '2017-6-15', null);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1D'), 'cuota mensual',  10, '2017-6-16', 'PAID_CONFIRMED', '2017-6-16', 'TRANSFER', 'PNG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1D'), 'cuota mensual',  10, '2017-6-27', 'REJECTED', '2017-6-27', 'TRANSFER', 'PNG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1A'), 'cuota mensual',  10, '2017-7-1', 'PAID_CONFIRMED', '2017-7-1', 'TRANSFER', 'PNG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method)
values((select id from apartments where name='1D'), 'cuota mensual',  10, '2017-7-1', 'PENDING', '2017-7-1', null);

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1D'), 'consumo de gas',  100, '2017-7-1',  'PAID_AWAITING_CONFIRMATION', '2017-7-1', 'CHECK', 'JPG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1D'), 'consumo de gas',  200, '2017-7-1',  'REJECTED', '2017-7-1', 'DEPOSIT', 'JPG');

insert into bills (apartment, description, due_amount, due_date, payment_status, last_update_on, payment_method, proof_of_payment_extension)
values((select id from apartments where name='1A'), 'iluminación del pasillo',  350, '2017-7-10',  'PAID_AWAITING_CONFIRMATION', '2017-7-10', 'CHECK', 'PNG');

insert into outlays (amount, category, condo, created_on, supplier, comment) values (15, 'SECURITY', 1, '2017-6-16', 'Watchman Dominicana', '');
insert into outlays (amount, category, condo, created_on, supplier, comment) values (10, 'REPARATION', 1, '2017-7-16', 'Edenorte', 'Reparación Lámpara Pasillo');
insert into outlays (amount, category, condo, created_on, supplier, comment) values (10, 'REPARATION', 2, '2017-6-16', 'Edenorte', 'Reparación Lámpara Principal');
insert into outlays (amount, category, condo, created_on, supplier, comment) values (55.36, 'SECURITY', 2, '2017-8-16', 'Watchman & Asocs', '');