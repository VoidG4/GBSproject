create table admin(
	id serial primary key,
	name text,
	surname text,
	username text unique,
	password text unique,
	email text unique
);

create table tutor(
	id serial primary key,
	name text,
	surname text,
	username text unique,
	password text unique,
	email text unique,
	field text
);

create table student(
	id serial primary key,
	name text,
	surname text,
	username text unique,
	password text unique,
	email text unique
);

create table course(
	id serial primary key,
	name text,
	description text,
	tutor_id int,
	foreign key(tutor_id) references tutor(id) on delete cascade
);

create table student_course (
	student_id int,
	course_id int,
	grade int,
	primary key(student_id, course_id),
	foreign key(student_id) references student(id) on delete cascade,
	foreign key(course_id) references course(id) on delete cascade
);

create table section (
	id serial primary key,
	course_id int,
	title text,
	description text,
	section_order int,
	foreign key(course_id) references course(id) on delete cascade
);

create table section_content(
	id serial primary key,
	section_id int,
	title text,
	content_type text check (content_type in ('text', 'video', 'image')),
	content text,
	content_order int,
	foreign key(section_id) references section(id) on delete cascade
);

create table quiz(
	id serial primary key,
	course_id int,
	title text,
	description text,
	passing_score int,
	foreign key(course_id) references course(id) on delete cascade
);

create table student_quiz (
	id serial primary key,
	student_id int,
	quiz_id int,
	score int,
	passed boolean,
	attempt_date timestamp default current_timestamp,
	foreign key(student_id) references student(id) on delete cascade,
	foreign key(quiz_id) references quiz(id) on delete cascade
);

create table question (
	id serial primary key,
	quiz_id int,
	text text,
	question_type text check (question_type in ('multiple_choice', 'true_false', 'short_answer')),
    	foreign key(quiz_id) references quiz(id) on delete cascade
);

create table question_option(
	id serial primary key,
	question_id int,
	option_text text,
	is_correct boolean,
	foreign key(question_id) references question(id) on delete cascade
);

create table chat (
	id serial primary key,
	user_id integer,
	title text, 
	created_at timestamp default current_timestamp
);

create table message (
	id serial primary key,
	chat_id integer,
	sender text check(sender in('user', 'gemini')),
	message text,
	created_at timestamp default current_timestamp,
	foreign key(chat_id) references chat(id) on delete cascade
);
