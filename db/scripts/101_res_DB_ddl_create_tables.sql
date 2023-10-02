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