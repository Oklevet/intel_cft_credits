drop SEQUENCE serial;
CREATE SEQUENCE serial START 101;

alter database intel_cft_credits reset default_text_search_config;
alter role postgres reset default_text_search_config;

drop table "Z#VID_DEBT";
drop table "Z#TAKE_IN_DEBT";
drop table "Z#VID_OPER_DOG";
drop table "Z#PLAN_OPER";
drop table "Z#FACT_OPER";
drop table "Z#PR_CRED";

CREATE SEQUENCE serial START 101;

truncate table "Z#VID_DEBT";
truncate table "Z#TAKE_IN_DEBT";
truncate table "Z#VID_OPER_DOG";
truncate table "Z#PLAN_OPER";
truncate table "Z#FACT_OPER";
truncate table "Z#PR_CRED";

commit;

select * from "Z#PLAN_OPER";
select count(1) from "Z#PR_CRED";
select * from "Z#PR_CRED";
select * from "Z#PR_CRED" pr
where pr.c_num_dog like '11-2014/Л';

select * from "Z#VID_DEBT";

select * from "Z#VID_DEBT" v
where v.c_debt_type like 'Процен%';

update pg_database set encoding = pg_char_to_encoding('UTF-8')
where datname = 'intel_cft_credits'


select fo.c_SUMMA, fo.c_OPER, v.c_VID_DEBT, v.c_VID_DEBT_DT, fo.collection_id
from FACT_OPER fo, VID_OPER_DOG v
where fo.collection_id in (
    select pr.c_LIST_PAY
    from   PR_CRED pr
    where  pr.C_COM_STATUS = 'WORK'
            and pr.id = 246792143)
    and fo.c_DATE < current_date + 1
    and fo.c_OPER = v.id;



create table "Z#VID_DEBT"(
	 ID bigint PRIMARY KEY
	,C_NAME varchar(500)
	,C_CODE varchar(100)
	,C_DEBT_TYPE varchar(50)
);


create table "Z#TAKE_IN_DEBT"(
	 ID bigint  		PRIMARY KEY
	,collection_id  	bigint
	,C_DEBT 	        bigint					--ссылка на вид задолженности
	,C_DT	 	        boolean
);


create table "Z#VID_OPER_DOG"(
	 ID 				bigint  PRIMARY KEY
	,C_NAME           	varchar(500)
	,C_CODE           	varchar(50)
	,C_TAKE_DEBT 		bigint			--участие в задолженности
	,C_VID_DEBT 		bigint			--вид погашаемой задолженности
	,C_VID_DEBT_DT 		bigint			--вид увеличиваемой задолженности
);



create table "Z#PLAN_OPER"(
	 ID 				bigint  PRIMARY KEY
	,C_DATE 			date
	,C_SUMMA 			NUMERIC(25,4)
	,C_VALUTA 			varchar(3)
	,C_OPER 			bigint
	,collection_id 		bigint
);


create table "Z#FACT_OPER"(
	 ID 			bigint  PRIMARY KEY
	,C_DATE 			date
	,C_SUMMA 			NUMERIC(25,4)
	,C_VALUTA 			varchar(3)
	,C_OPER 			bigint
	,collection_id 	bigint
);


create table "Z#PR_CRED"(
	 ID 				bigint  PRIMARY KEY
	,C_NUM_DOG			varchar(30)
	,C_DATE_BEG 		date
	,C_DATE_END 		date
	,C_SUMMA 			NUMERIC(25,4)
	,C_FT_CREDIT		varchar(3)
	,C_KIND_CREDIT		varchar(250)
	,C_LIST_PAY 		bigint
	,C_LIST_PLAN_PAY 	bigint
	,C_COM_STATUS		varchar(30)
);

create table "Z#PR_CRED_TEST"(
	 ID 				bigint  PRIMARY KEY
	,C_NUM_DOG			varchar(30)
	,C_DATE_BEG 		date
	,C_DATE_END 		date
	,C_SUMMA 			NUMERIC(25,4)
	,C_FT_CREDIT		varchar(3)
	,C_KIND_CREDIT		varchar(250)
	,C_LIST_PAY 		bigint
	,C_LIST_PLAN_PAY 	bigint
	,C_COM_STATUS		varchar(30)
);

commit;

select p.id, po.id, po.c_oper, po.c_summa, vp.c_code, vp.id
from "Z#PR_CRED" p, "Z#PLAN_OPER" po, "Z#VID_OPER_DOG" vp
    , "Z#FACT_OPER" fo, "Z#VID_OPER_DOG" vf
where p.id = 3177010213
    and p.c_list_plan_pay = po.collection_id
        and po.c_oper = vp.id


select p.id, fo.id, fo.c_oper, fo.c_summa
from "Z#PR_CRED" p, "Z#FACT_OPER" fo
where p.c_list_pay = fo.collection_id
    and p.id = 3177010213   -- in (3177010213, 3176883024)


group by p.id
having count(po.collection_id) > 3
order by count(po.collection_id)
