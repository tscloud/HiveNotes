/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 12.0 		*/
/*  Created On : 15-Aug-2015 18:03:13 				*/
/*  DBMS       : SQLite 								*/
/* ---------------------------------------------------- */

/* Drop Tables */

DROP TABLE IF EXISTS 'Apiary'
;

DROP TABLE IF EXISTS 'Hive'
;

DROP TABLE IF EXISTS 'Note'
;

DROP TABLE IF EXISTS 'Picture'
;

DROP TABLE IF EXISTS 'Notification'
;

DROP TABLE IF EXISTS 'Profile'
;

DROP TABLE IF EXISTS 'LogEntry'
;

/* Create Tables with Primary and Foreign Keys, Check and Unique Constraints */

CREATE TABLE 'Apiary'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 0,
	'PROFILE' INTEGER,
	'PROFILE_TABLE' INTEGER,
	'NAME' TEXT,
	CONSTRAINT 'FK_Apiary_Profile' FOREIGN KEY ('PROFILE') REFERENCES 'Profile' ('_ID') ON DELETE No Action ON UPDATE Cascade
)
;

CREATE TABLE 'Hive'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 2,
	'BEEHOUSE' INTEGER NOT NULL,
	'PARENT' INTEGER NOT NULL,
	'NAME' TEXT
)
;

CREATE TABLE 'Note'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 4,
	'BEEHOUSE' INTEGER NOT NULL,
	'PARENT' INTEGER NOT NULL,
	'TYPE' TEXT,
	CONSTRAINT 'FK_Note_Apiary' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Note_Hive' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Picture'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 3,
	'BEEHOUSE' INTEGER NOT NULL,
	'PARENT' INTEGER NOT NULL,
	'IMAGE_URI' TEXT,
	CONSTRAINT 'FK_Picture_Apiary' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Picture_Hive' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Notification'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 1,
	'BEEHOUSE' INTEGER NOT NULL,
	'PARENT' INTEGER NOT NULL,
	'ALARM_URI' TEXT,
	CONSTRAINT 'FK_Notification_Hive' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Profile'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 5,
	'NAME' TEXT,
	'EMAIL' TEXT
)
;

CREATE TABLE 'LogEntry'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'_TABLE_ID' INTEGER NOT NULL DEFAULT 6,
	'BEEHOUSE' INTEGER NOT NULL,
	'PARENT' INTEGER NOT NULL,
	'VISIT_DATE' TEXT,
	CONSTRAINT 'FK_LogEntry_Hive' FOREIGN KEY ('BEEHOUSE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;
