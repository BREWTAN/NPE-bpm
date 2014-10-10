use npe;


DROP TABLE IF EXISTS procdef;

CREATE TABLE procdef (
  defid VARCHAR(48) NOT NULL,
  defname VARCHAR(64),
  version VARCHAR(64),
  packages VARCHAR(64),
  xmlbody LONGTEXT NULL,
  subelements VARCHAR(512),
  createtime decimal(16,0) ,
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
	submittime decimal(16,0) ,
	obtainer   varchar(64),  
	obtaintime decimal(16,0),
	createtime decimal(16,0),
	taskcenter	varchar(64),
	rootproc	varchar(64),
PRIMARY KEY (taskinstid)) CHARACTER SET = gbk  ENGINE=InnoDB;

ALTER TABLE tasks 
ADD INDEX index_procinst (procinstid ASC);


DROP TABLE IF EXISTS tasks_obtain;

create table tasks_obtain(
	taskinstid   varchar(48) not null,
	interstate integer not null default 0,
	obtainer   varchar(64),  
	obtaintime decimal(16,0),
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
	submittime decimal(16,0) ,
PRIMARY KEY (taskinstid)) CHARACTER SET = gbk  ENGINE=InnoDB;


DROP TABLE IF EXISTS taskcenter;

create table taskcenter(
	centerid   varchar(48) not null,
	centername varchar(64),  
	createtime decimal(16,0),
	modtime	decimal(16,0),
PRIMARY KEY (centerid)) CHARACTER SET = gbk  ENGINE=InnoDB;


DROP TABLE IF EXISTS taskrole;

create table taskrole(
	roleid   varchar(48) not null,
	rolename varchar(64),  
	centerid decimal(16,0),
	createtime decimal(16,0),
	modtime	decimal(16,0),
PRIMARY KEY (roleid)) CHARACTER SET = gbk  ENGINE=InnoDB;




DROP TABLE IF EXISTS params;

create table params(
  keyy  varchar(256) not null,
  valuee varchar(512) not null,

primary key(keyy)) CHARACTER SET = gbk;

SET GLOBAL max_connections = 500;
