-- Дать права пользователю ewm_user на схему public
GRANT ALL ON SCHEMA public TO ewm_user;
GRANT CREATE ON SCHEMA public TO ewm_user;
ALTER SCHEMA public OWNER TO ewm_user;

-- Настроить права по умолчанию для будущих таблиц
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO ewm_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO ewm_user;