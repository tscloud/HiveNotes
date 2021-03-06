/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 12.0 		*/
/*  Created On : 18-Aug-2015 12:54:31 					*/
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

CREATE TABLE 'Profile'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'NAME' TEXT,
	'EMAIL' TEXT
)
;

CREATE TABLE 'Apiary'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'PROFILE' INTEGER NOT NULL,
	'NAME' TEXT,
	'POSTAL_CODE' TEXT,
	'LATITUDE' REAL,
	'LONGITUDE' REAL,
	CONSTRAINT 'FK_Apiary_Profile' FOREIGN KEY ('PROFILE') REFERENCES 'Profile' ('_ID') ON DELETE No Action ON UPDATE Cascade
)
;

CREATE TABLE 'Hive'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER NOT NULL,
	'NAME' TEXT,
	'SPECIES' TEXT,
	'REQUEEN' TEXT,
	'FOUNDATION_TYPE' TEXT,
	'NOTE' TEXT,
	CONSTRAINT 'FK_Hive_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Note'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER,
	'HIVE' INTEGER,
	'TYPE' TEXT,
	'NOTE_TEXT' TEXT,
	CONSTRAINT 'FK_Note_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Note_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Picture'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER,
	'HIVE' INTEGER,
	'IMAGE_URI' TEXT,
	CONSTRAINT 'FK_Picture_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Picture_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Notification'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER,
	'HIVE' INTEGER,
	'EVENT_ID' INTEGER NOT NULL,
	'RMNDR_TYPE' INTEGER NOT NULL,
	'RMNDR_DESC' TEXT,
	CONSTRAINT 'FK_Notification_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Notification_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade,
	CONSTRAINT 'FK_Notification_Rmndr_Type' FOREIGN KEY ('RMNDR_TYPE') REFERENCES 'Notification_Type' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'Notification_Type'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY,
	'LOG_TYPE' TEXT NOT NULL,
	'DESCRIPTION' TEXT NOT NULL
)
;

/* MAY NOT USE - VALUES CURRENTLY MAINTAINED IN STATIC CLASS */
/* This is where the types of Notifications are defined */
/* This is NOT a completely externalized definition as code that creates Notifications */
/*    will have to know Notification type regardless of its definition here */
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (0, 'LogEntryOther', 'Add Honey supers');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (1, 'LogEntryOther', 'Remove drone comb');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (2, 'LogEntryFeeding', 'Feed sugar syrup');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (3, 'LogEntryHiveHealth', 'Remove mite treament');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (4, 'LogEntryGeneral', 'Check for laying queen');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (5, 'LogEntryOther', 'Add mouse guard');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (6, 'LogEntryOther', 'Spring inspection');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (7, 'LogEntryOther', 'Treat for mites');
INSERT INTO 'Notification_Type' ('_ID', 'LOG_TYPE', 'DESCRIPTION') VALUES (8, 'LogEntryOther', 'Other');

CREATE TABLE 'Weather'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER,
	'SNAPSHOT_DATE' INTEGER,
	'TEMPERATURE' TEXT,
	'RAINFALL' TEXT,
	'PRESSURE' TEXT,
	'WEATHER' TEXT,
	'WINDDIRECTION' TEXT,
	'WINDMPH' TEXT,
	'HUMIDITY' TEXT,
	'DEWPOINT' TEXT,
	'VISIBILITY' TEXT,
	'SOLARRADIATION' TEXT,
	'UVINDEX' TEXT,
	'POLLEN_COUNT' TEXT,
	'POLLUTION' TEXT,
	CONSTRAINT 'FK_Weather_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'WeatherHistory'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'APIARY' INTEGER,
	'SNAPSHOT_DATE' INTEGER,
	'FOG' TEXT,
	'RAIN' TEXT,
	'SNOW' TEXT,
	'THUNDER' TEXT,
	'HAIL' TEXT,
	'MAXTEMPI' TEXT,
    'MINTEMPI' TEXT,
    'MAXDEWPTI' TEXT,
    'MINDEWPTI' TEXT,
    'MAXPRESSUREI' TEXT,
    'MINPRESSUREI' TEXT,
    'MAXWSPDI' TEXT,
    'MINWSPDI' TEXT,
    'MEANWDIRD' TEXT,
    'MAXHUMIDITY' TEXT,
    'MINHUMIDITY' TEXT,
    'MAXVISI' TEXT,
    'MINVISI' TEXT,
    'PRECIPI' TEXT,
    'COOLINGDEGREEDAYS' TEXT,
    'HEATINGDEGREEDAYS' TEXT,

	CONSTRAINT 'FK_WeatherHistory_Apiary' FOREIGN KEY ('APIARY') REFERENCES 'Apiary' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'LogEntryGeneral'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'HIVE' INTEGER NOT NULL,
	'VISIT_DATE' INTEGER,
	'POPULATION' TEXT,
	'TEMPERAMENT' TEXT,
	'BROOD_PATTERN' TEXT,
	'QUEEN' TEXT,
	'HONEY_STORES' TEXT,
	'POLLEN_STORES' TEXT,
	CONSTRAINT 'FK_LogEntryGeneral_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'LogEntryProductivity'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'HIVE' INTEGER NOT NULL,
	'VISIT_DATE' INTEGER,
	'EXTRACTED_HONEY' REAL,
	'POLLEN_COLLECTED' REAL,
	'BEESWAX_COLLECTED' REAL,
	CONSTRAINT 'FK_LogEntryProductivity_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'LogEntryHiveHealth'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'HIVE' INTEGER NOT NULL,
	'VISIT_DATE' INTEGER,
	'PESTS_DETECTED' TEXT,
	'DISEASE_DETECTED' TEXT,
	'VARROA_TREATMENT' TEXT,
	CONSTRAINT 'FK_LogEntryPestMgmt_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'LogEntryFeeding'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'HIVE' INTEGER NOT NULL,
	'VISIT_DATE' INTEGER,
	'FEEDING_TYPES' TEXT,
	CONSTRAINT 'FK_LogEntryFeeding_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'LogEntryOther'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'HIVE' INTEGER NOT NULL,
	'VISIT_DATE' INTEGER,
	'REQUEEN' TEXT,
	CONSTRAINT 'FK_LogEntryOther_Hive' FOREIGN KEY ('HIVE') REFERENCES 'Hive' ('_ID') ON DELETE Cascade ON UPDATE Cascade
)
;

CREATE TABLE 'GraphableData'
(
	'_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	'DIRECTIVE' TEXT,
	'COLUMN' TEXT,
	'PRETTY_NAME' TEXT,
	'CATEGORY' TEXT,
	'KEY_LEVEL' TEXT
)
;

/* These are the set of data that can be presented in a graph */
/*  If DIRECTIVE/COLUMN given => data will be presented as that value vs time */
/*  Otherwise => special action to be taken */
/*  Only graphs that have TABLE/COLUMN can be presented against eachother */
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (0, 'Weather', 'pollen_count', 'Pollen Count', 'Observed Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (1, 'Weather', 'temperature', 'Temperature', 'Observed Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (2, 'Weather', 'pressure', 'Pressure', 'Observed Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (3, 'N/A', 'N/A', 'Pollen Type', 'Observed Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (4, 'WeatherHistory', 'maxpressurei', 'Max Pressure', 'Historic Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (5, 'WeatherHistory', 'minpressurei', 'Min Pressure', 'Historic Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (6, 'WeatherHistory', 'maxtempi', 'Max Temperature', 'Historic Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (7, 'WeatherHistory', 'mintempi', 'Min Temperature', 'Historic Weather', 'A');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (8, 'LogEntryProductivity', 'extracted_honey', 'Honey', 'Productivity', 'H');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (9, 'LogEntryProductivity', 'pollen_collected', 'Pollen', 'Productivity', 'H');
INSERT INTO 'GraphableData' ('_ID', 'DIRECTIVE', 'COLUMN', 'PRETTY_NAME', 'CATEGORY', 'KEY_LEVEL') VALUES (10, 'LogEntryProductivity', 'beeswax_collected', 'Beeswax', 'Productivity', 'H');

/* for testing ONLY  - just so we don't have to manually reenter when we drop the DB */
/* INSERT INTO 'Profile' ('_ID', 'NAME', 'EMAIL') VALUES (0, 'TC', 'TC@mail.com');
/* INSERT INTO 'Apiary' ('_ID', 'PROFILE', 'NAME', 'POSTAL_CODE') VALUES (0, 0, 'Grape Apiary', '02818');
/* INSERT INTO 'Hive' ('_ID', 'APIARY', 'NAME', 'SPECIES', 'FOUNDATION_TYPE') VALUES (0, 0, 'Hive 5', 'Killer', 'Wax');
