create table mail_user (
  id bigint identity,
  email varchar(256) not null unique,
  password varchar(256) not null,
  firstName varchar(256) not null,
  lastName varchar(256) not null
);
create table message (
  id bigint identity,
  subject varchar(256) not null,
  message varchar(500) not null,
  fromUser bigint,
  toUser bigint,
  FOREIGN KEY(fromUser) REFERENCES mail_user(id),
  FOREIGN KEY(toUser) REFERENCES mail_user(id)
);

insert into mail_user(id,email,password,firstName,lastName) values (1,'rob@example.org','penguin','Rob','Winch');
insert into mail_user(id,email,password,firstName,lastName) values (2,'luke@example.com','lion','Luke','Taylor');

insert into message (id,subject,message,fromUser,toUser) values (1,'Vulnerabilities Found?','I believe I found some vulnerabilities in the message application. It may be good to ensure that you secure the application.',2,1);
insert into message (id,subject,message,fromUser,toUser) values (2,'RE: Vulnerabilities Found?','Thanks you are right. I will get that fixed right away.',1,2);