CREATE TABLE IF NOT EXISTS data.users(
                                         id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name  VARCHAR(150) NOT NULL,
    second_name  VARCHAR(150) NOT NULL,
    email  VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(200),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    age INT,
    gender VARCHAR(15),
    weight REAL,
    target_weight REAL,
    height REAL,
    image VARCHAR(255),
    birth_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    refresh_token VARCHAR(255)
    );

COMMENT ON TABLE data.users IS 'Основная таблица пользователей: профиль, параметры тела, авторизационные данные';
COMMENT ON COLUMN data.users.first_name IS 'Имя пользователя';
COMMENT ON COLUMN data.users.second_name IS 'Фамилия пользователя';
COMMENT ON COLUMN data.users.email IS 'Email (уникальный, используется для входа)';
COMMENT ON COLUMN data.users.password IS 'Хэш пароля';
COMMENT ON COLUMN data.users.role IS 'Роль пользователя';
COMMENT ON COLUMN data.users.enabled IS 'Активен ли аккаунт';
COMMENT ON COLUMN data.users.age IS 'Возраст';
COMMENT ON COLUMN data.users.gender IS 'Пол';
COMMENT ON COLUMN data.users.weight IS 'Текущий вес (кг)';
COMMENT ON COLUMN data.users.target_weight IS 'Целевой вес (кг)';
COMMENT ON COLUMN data.users.height IS 'Рост (см)';
COMMENT ON COLUMN data.users.image IS 'Ссылка на аватар';
COMMENT ON COLUMN data.users.birth_date IS 'Дата рождения';
COMMENT ON COLUMN data.users.created_at IS 'Дата создания записи';
COMMENT ON COLUMN data.users.updated_at IS 'Дата последнего обновления профиля';