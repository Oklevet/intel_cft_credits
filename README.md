intel_cft_credits

1. Для запуска проекта, необходимо клонировать проект из репозитория https://github.com/Oklevet/intel_cft_credits.
2. Затем необходимо создать локальные базы данных "intel_cft_credits" и "intel_receiver" с одноимёнными схемами.
3. После - прописать логин и пароль к созданным базам данных в файлы src/main/resources/connectionCFT.properties и src/main/resources/connectionRecieveDB.properties.
4. Запустить скрипты 001-004 на схеме intel_cft_credits.intel_cft_credits и скрипт 101 на схеме intel_receiver.intel_receiver.

К проекту приложен файл Блок-схема сервиса и его реализации.drawio с описанием работы сервиса в блок-схемах.