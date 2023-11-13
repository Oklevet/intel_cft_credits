truncate table VID_DEBT;
truncate table TAKE_IN_DEBT;
truncate table VID_OPER_DOG;
truncate table PR_CRED;

insert into VID_DEBT values  (nextval('serial'), 'МСФО. Корректировка резерва срочной задолженности', 'CR_IFRS_CORR_REZ', 'Простая');
insert into VID_DEBT values  (nextval('serial'), 'МСФО. Денежный поток', 'CR_IFRS_FLOW', 'Простая');
insert into VID_DEBT values  (nextval('serial'), 'Сумма полного досрочного погашения', 'FULL_EARLY_REPAYMENT', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Плановая сумма погашения', 'SUM_DEBTS_ALL', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Резерв по срочной задолженности', 'V_РЕЗЕРВ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Аванс Неучтенные проценты за кредит', 'АВАНС_НЕУЧТЕН_ПРОЦЕНТЫ', 'Аванс');
insert into VID_DEBT values  (nextval('serial'),'Ссудная задолженность', 'КРЕДИТ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Вынесено кредита на просрочку', 'КРЕДИТ_ВЫНЕС', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Кредит + просрочка', 'КРЕДИТ_ПОЛН', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Неучтенные проценты за кредит', 'НЕУЧТЕН_ПРОЦЕНТЫ', 'Процентная');
insert into VID_DEBT values  (nextval('serial'),'Отложенные проценты', 'ОТЛОЖЕН_ПРОЦЕНТЫ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Погашено кредита', 'ПОГАШЕНО_КР', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Погашено процентов', 'ПОГАШЕНО_ПР', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Погашено просроченного кредита', 'КРЕДИТ_ПР_ГАШ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Просроченная ссудная задолженность', 'ПРОСРОЧ_КРЕДИТ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Резерв', 'РЕЗЕРВ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Резерв. сформированный за счет себестоимости', 'РЕЗЕРВ_СЕБЕСТ', 'Простая');
insert into VID_DEBT values  (nextval('serial'),'Учтенные проценты за кредит', 'УЧТЕН_ПРОЦЕНТЫ', 'Простая');


insert into VID_OPER_DOG values (nextval('serial'),'Выдача кредита', 'ВЫДАЧА_КРЕДИТА', nextval('serial'), null, (select id from VID_DEBT where CODE = 'КРЕДИТ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ВЫДАЧА_КРЕДИТА'), (select id from VID_DEBT where CODE = 'КРЕДИТ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'Авансовое гашение Неучтенные проценты за кредит', 'АГАШ_НЕУЧТЕН_ПРОЦЕНТЫ', nextval('serial'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'АВАНС_НЕУЧТЕН_ПРОЦЕНТЫ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'АГАШ_НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'АГАШ_НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_ПР'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'АГАШ_НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'АВАНС_НЕУЧТЕН_ПРОЦЕНТЫ'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Вынос задолженности по кредиту', 'ВЫНОС_ЗАДОЛЖ_КР', nextval('serial'), (select id from VID_DEBT where CODE = 'КРЕДИТ'), (select id from VID_DEBT where CODE = 'ПРОСРОЧ_КРЕДИТ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ВЫНОС_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_ПР'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ВЫНОС_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'ПРОСРОЧ_КРЕДИТ'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ВЫНОС_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ВЫНОС_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'КРЕДИТ_ВЫНЕС'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Гашение задолженности по кредиту', 'ГАШЕН_ЗАДОЛЖ_КР', nextval('serial'), (select id from VID_DEBT where CODE = 'ПРОСРОЧ_КРЕДИТ'), null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'ПРОСРОЧ_КРЕДИТ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_КР'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'КРЕДИТ_ПОЛН'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'КРЕДИТ_ПР_ГАШ'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_ЗАДОЛЖ_КР'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), false);


insert into VID_OPER_DOG values (nextval('serial'),'Гашение кредита', 'ГАШЕНИЕ_КРЕД', nextval('serial'), (select id from VID_DEBT where CODE = 'КРЕДИТ'), null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_КРЕД'), (select id from VID_DEBT where CODE = 'КРЕДИТ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_КРЕД'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_КР'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_КРЕД'), (select id from VID_DEBT where CODE = 'КРЕДИТ_ПОЛН'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_КРЕД'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Гашение учтенных процентов', 'ГАШЕН_УЧТЕН_ПРОЦ', nextval('serial'), null, (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_УЧТЕН_ПРОЦ'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_УЧТЕН_ПРОЦ'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_ПР'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕН_УЧТЕН_ПРОЦ'), (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'Капитализация учтенных процентов', 'УЧЕТ_ОТЛОЖ_ПРОЦ_КЗК', nextval('serial'), (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'ОТЛОЖЕН_ПРОЦЕНТЫ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УЧЕТ_ОТЛОЖ_ПРОЦ_КЗК'), (select id from VID_DEBT where CODE = 'ОТЛОЖЕН_ПРОЦЕНТЫ'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УЧЕТ_ОТЛОЖ_ПРОЦ_КЗК'), (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'МСФО. Отрицательная корректировка РВПС', 'CR_IFRS_RVPS_N', nextval('serial'), null, null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'CR_IFRS_RVPS_N'), (select id from VID_DEBT where CODE = 'CR_IFRS_CORR_REZ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'МСФО. Положительная корректировка РВПС', 'CR_IFRS_RVPS_P', nextval('serial'), null, null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'CR_IFRS_RVPS_P'), (select id from VID_DEBT where CODE = 'CR_IFRS_CORR_REZ'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Списание авансового платежа Неучтенные проценты за кредит', 'АУЧ_НЕУЧТЕН_ПРОЦЕНТЫ', nextval('serial'), (select id from VID_DEBT where CODE = 'АВАНС_НЕУЧТЕН_ПРОЦЕНТЫ'), null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'АУЧ_НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'АУЧ_НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'АВАНС_НЕУЧТЕН_ПРОЦЕНТЫ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'Увеличение отнесенного на себестоимость резерва', 'УВЕЛИЧ_НА_СЕБЕСТ', nextval('serial'), null, (select id from VID_DEBT where CODE = 'КРЕДИТ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УВЕЛИЧ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'РЕЗЕРВ'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УВЕЛИЧ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'V_РЕЗЕРВ'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УВЕЛИЧ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'РЕЗЕРВ_СЕБЕСТ'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Уменьшение отнесенного на себестоимость резерва', 'УМЕНЬШ_НА_СЕБЕСТ', nextval('serial'), null, (select id from VID_DEBT where CODE = 'КРЕДИТ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УМЕНЬШ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'РЕЗЕРВ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УМЕНЬШ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'V_РЕЗЕРВ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УМЕНЬШ_НА_СЕБЕСТ'), (select id from VID_DEBT where CODE = 'РЕЗЕРВ_СЕБЕСТ'), false);


insert into VID_OPER_DOG values (nextval('serial'),'Учет процентов за кредит', 'УЧЕТ_ПРОЦ_ЗА_КР', nextval('serial'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'));
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УЧЕТ_ПРОЦ_ЗА_КР'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), false);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'УЧЕТ_ПРОЦ_ЗА_КР'), (select id from VID_DEBT where CODE = 'УЧТЕН_ПРОЦЕНТЫ'), true);


insert into VID_OPER_DOG values (nextval('serial'),'Гашение процентов', 'ГАШЕНИЕ_ПРОЦ', nextval('serial'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), null);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_ПРОЦ'), (select id from VID_DEBT where CODE = 'ПОГАШЕНО_ПР'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_ПРОЦ'), (select id from VID_DEBT where CODE = 'CR_IFRS_FLOW'), true);
		insert into TAKE_IN_DEBT values (nextval('serial'),(select TAKE_DEBT from VID_OPER_DOG where CODE = 'ГАШЕНИЕ_ПРОЦ'), (select id from VID_DEBT where CODE = 'НЕУЧТЕН_ПРОЦЕНТЫ'), false);


insert into PR_CRED
  values (nextval('serial'),'Р10-5000', to_date('13.09.2022', 'dd.mm.yyyy'), to_date('13.09.2032', 'dd.mm.yyyy'), 5000000, 'RUB', 'Аннуитетный потреительский', nextval('serial'), nextval('serial'), 'Открыт');

insert into PR_CRED
	values (nextval('serial'),'Р7-450000-11', to_date('22.12.2018', 'dd.mm.yyyy'), to_date('22.11.2025', 'dd.mm.yyyy'), 4500000, 'RUB', 'Аннуитетный потреительский', nextval('serial'), nextval('serial'), 'Открыт');

insert into PR_CRED
	values (nextval('serial'),'Д4-150000-8', to_date('20.10.2020', 'dd.mm.yyyy'), to_date('20.08.2024', 'dd.mm.yyyy'), 150000, 'USD', 'Аннуитетный потреительский', nextval('serial'), nextval('serial'), 'Открыт');