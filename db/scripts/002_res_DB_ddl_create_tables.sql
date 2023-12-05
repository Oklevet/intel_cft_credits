drop SEQUENCE serial;
drop table PR_CRED;
drop table DEBTS;
CREATE SEQUENCE serial START 100001;

truncate table PR_CRED;
truncate table DEBTS;
commit;

create table PR_CRED(
	 ID 			bigint unique not null
	,NUM_DOG		varchar(30)
	,DEBTS			bigint unique
);

create table DEBTS(
	 ID 			bigint
	,CODE 			varchar(50)
	,SUMMA 			NUMERIC(25,4)
	,collection_id	bigint
);

select * from PR_CRED;
select * from DEBTS;
--                                      1 try       2try
--                                    25  th                                    main th
select count(1) from PR_CRED;         --7688       7688                        --7688
select count(1) from DEBTS;           --1309489    1313183   1615252           --302130    302130

--                    with sleep
--                                      1309208    1312295   1314313  1315243
commit;

select count(1) from debts d, pr_cred p --670321   672151    827076             --155162    155162
where p.debts = d.collection_id
        and d.summa != 0

--                    with sleep
--                                      670047    671367   673025     673425

select sum(d.summa) from debts d, pr_cred p
where p.debts = d.collection_id
        and d.summa != 0
