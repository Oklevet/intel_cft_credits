drop SEQUENCE serial;
drop table VID_DEBT;
drop table TAKE_IN_DEBT;
drop table VID_OPER_DOG;
drop table PLAN_OPER;
drop table FACT_OPER;
drop table PR_CRED;

CREATE SEQUENCE serial START 101;

create table VID_DEBT(
	 ID bigint PRIMARY KEY
	,NAME varchar(500)
	,CODE varchar(50)   unique not null
	,DEBT_TYPE varchar(50)
);


create table TAKE_IN_DEBT(
	 ID bigint  PRIMARY KEY
	,collection_id  bigint
	,DEBT 	        bigint					--ссылка на вид задолженности
	,DT	 	        boolean
);


create table VID_OPER_DOG(
	 ID bigint  PRIMARY KEY
	,NAME           varchar(500)
	,CODE           varchar(50)
	,TAKE_DEBT 		bigint			--участие в задолженности
	,VID_DEBT 		bigint			--вид погашаемой задолженности
	,VID_DEBT_DT 	bigint			--вид увеличиваемой задолженности
);



create table PLAN_OPER(
	 ID 			bigint  PRIMARY KEY
	,DATE 			date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,OPER 			bigint
	,collection_id 	bigint
);


create table FACT_OPER(
	 ID 			bigint  PRIMARY KEY
	,DATE 			date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,OPER 			bigint
	,collection_id 	bigint
);


create table PR_CRED(
	 ID 			bigint  PRIMARY KEY
	,NUM_DOG		varchar(30)
	,DATE_BEG 		date
	,DATE_END 		date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,KIND_CREDIT	varchar(50)
	,LIST_PAY 		bigint
	,LIST_PLAN_PAY 	bigint
	,STATE			varchar(30)
);

commit;
