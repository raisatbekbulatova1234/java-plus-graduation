-- Дать права пользователю stats_user на схему public
GRANT ALL ON SCHEMA public TO stats_user;
GRANT CREATE ON SCHEMA public TO stats_user;
ALTER SCHEMA public OWNER TO stats_user;

-- Настроить права по умолчанию
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO stats_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO stats_user;