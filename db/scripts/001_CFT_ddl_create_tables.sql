grant usage on schema intel_cft_credits to public;
grant create on schema intel_cft_credits to public;

truncate table VID_DEBT;
truncate table TAKE_IN_DEBT;
truncate table VID_OPER_DOG;
truncate table PLAN_OPER;q
truncate table FACT_OPER;
truncate table PR_CRED;

drop table VID_DEBT;
drop table TAKE_IN_DEBT;
drop table VID_OPER_DOG;
drop table PLAN_OPER;q
drop table FACT_OPER;
drop table PR_CRED;
drop SEQUENCE serial;

CREATE SEQUENCE serial START 101;

create table VID_DEBT(
	 ID int  PRIMARY KEY
	,NAME varchar(500)
	,CODE varchar(50)
	,DEBT_TYPE varchar(50)
);


create table TAKE_IN_DEBT(
	 ID int  PRIMARY KEY
	,collection_id  int
	,DEBT 	        int					--ссылка на вид задолженности
	,DT	 	        boolean
);


create table VID_OPER_DOG(
	 ID int  PRIMARY KEY
	,NAME           varchar(500)
	,CODE           varchar(50)
	,TAKE_DEBT 		int			--участие в задолженности
	,VID_DEBT 		int			--вид погашаемой задолженности
	,VID_DEBT_DT 	int			--вид увеличиваемой задолженности
);



create table PLAN_OPER(
	 ID 			int  PRIMARY KEY
	,DATE 			date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,OPER 			int
	,collection_id 	int
);


create table FACT_OPER(
	 ID 			int  PRIMARY KEY
	,DATE 			date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,OPER 			int
	,collection_id 	int
);


create table PR_CRED(
	 ID 			int  PRIMARY KEY
	,NUM_DOG		varchar(30)
	,DATE_BEG 		date
	,DATE_END 		date
	,SUMMA 			NUMERIC(16,9)
	,VAL 			varchar(3)
	,KIND_CREDIT	varchar(50)
	,LIST_PAY 		int
	,LIST_PLAN_PAY 	int
	,STATE			varchar(30)
);

select * from vid_debt;
select * from vid_oper_dog;
select * from TAKE_IN_DEBT;
