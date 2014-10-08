use npe;


DROP TABLE IF EXISTS procdef;

CREATE TABLE procdef (
  defid VARCHAR(48) NOT NULL,
  defname VARCHAR(64),
  topid VARCHAR(48),
  version VARCHAR(64),
  packages VARCHAR(64),
  xmlbody LONGTEXT NULL,
  subelements VARCHAR(512),
  createtime LONG ,
  PRIMARY KEY (defid)) CHARACTER SET = gbk; 


DROP TABLE IF EXISTS tasks;

create table tasks(
	taskinstid   varchar(48) not null,
	procdefid  varchar(48) not null,
	procinstid varchar(48) not null,
	taskdefid  varchar(48) not null,
	interstate integer not null default 0,
	previnsts text ,
	antecessors text,
	procPIO integer ,
	taskPIO integer ,
	rolemark text,
	startMS long,
	idata1 integer ,
	idata2 integer ,
	strdata1 text,
	strdata2 text,
	fdata1 float,
	fdata2 float,
	taskname varchar(64),
	jsondata longtext,
	submitter  varchar(64),
	submittime long ,
	obtainer   varchar(64),  
	obtaintime long,
	createtime long,
PRIMARY KEY (taskinstid)) CHARACTER SET = gbk  ENGINE=InnoDB;


DROP TABLE IF EXISTS tasks_obtain;

create table tasks_obtain(
	taskinstid   varchar(48) not null,
	interstate integer not null default 0,
	obtainer   varchar(64),  
	obtaintime long,
PRIMARY KEY (taskinstid)) CHARACTER SET = gbk  ENGINE=InnoDB;


DROP TABLE IF EXISTS tasks_submit;

create table tasks_submit(
	taskinstid   varchar(48) not null,
	interstate integer not null default 0,
	procPIO integer ,
	taskPIO integer ,
	rolemark text,
	idata1 integer ,
	idata2 integer ,
	strdata1 text,
	strdata2 text,
	fdata1 float,
	fdata2 float,
	taskname varchar(64),
	jsondata longtext,
	submitter  varchar(64),
	submittime long ,
PRIMARY KEY (taskinstid)) CHARACTER SET = gbk  ENGINE=InnoDB;



DROP TABLE IF EXISTS convergecounter;

create table convergecounter(
	keyy	INT NOT NULL AUTO_INCREMENT ,
	convergeid		varchar(128) not null,
	taskinstids   longtext,
	state int default 0,
PRIMARY KEY (keyy)) CHARACTER SET = gbk  ENGINE=InnoDB;


DROP TABLE IF EXISTS procinsts;

create table procinsts(
  procdefid  varchar(32) not null,
  procinstid  varchar(32) not null,
PRIMARY KEY (procinstid)) CHARACTER SET = gbk;

DROP TABLE IF EXISTS params;

create table params(
  keyy  varchar(256) not null,
  valuee varchar(512) not null,

primary key(keyy)) CHARACTER SET = gbk;

SET GLOBAL max_connections = 500;
