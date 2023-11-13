drop SEQUENCE serial;
drop table VID_DEBT;
drop table TAKE_IN_DEBT;
drop table VID_OPER_DOG;
drop table PLAN_OPER;
drop table FACT_OPER;
drop table PR_CRED;

CREATE SEQUENCE serial START 101;

create table VID_DEBT(
	 ID int  PRIMARY KEY
	,NAME varchar(500)
	,CODE varchar(50)   unique not null
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

select * from PR_CRED -- 1148

select fo.SUMMA, fo.OPER, v.VID_DEBT, v.VID_DEBT_DT, fo.collection_id
                     from FACT_OPER fo, VID_OPER_DOG v
                     where  fo.DATE < current_date + 1
                     and fo.OPER = v.id