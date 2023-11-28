drop SEQUENCE serial;
drop table PR_CRED;
drop table DEBTS;
CREATE SEQUENCE serial START 100001;

truncate table PR_CRED;
truncate table DEBTS;

create table PR_CRED(
	 ID 			int
	,NUM_DOG		varchar(30)
	,DEBTS			int
);

create table DEBTS(
	 ID 			int
	,CODE 			varchar(50)
	,SUMMA 			NUMERIC(25,4)
	,collection_id	int
);

select * from PR_CRED;
select * from DEBTS;

commit;