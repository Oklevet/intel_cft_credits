grant usage on schema intel_receiver to public;
grant create on schema intel_receiver to public;

drop SEQUENCE serial;
CREATE SEQUENCE serial START 100001;

create table PR_CRED(
	 ID 			int
	,NUM_DOG		varchar(30)
	,DEBTS			int
);

create table DEBTS(
	 ID 			int
	,CODE 			varchar(50)
	,SUMMA 			NUMERIC(16,9)
	,collection_id	int
);


---- Test scripts -----
select nextval('serial');

truncate table pr_cred;
truncate table debts;

select * from PR_CRED;
select * from DEBTS;

select p.num_dog, d.code, d.summa
from PR_CRED p, debts d
where p.debts = d.collection_id;