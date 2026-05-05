CREATE TABLE IF NOT EXISTS data.event (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
 text TEXT NOT NULL,
 start_time VARCHAR(100) NOT NULL,
 end_time VARCHAR(100),
 date VARCHAR(100) NOT NULL,
 completed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_events_user_id ON data.event(user_id);

COMMENT ON TABLE data.event IS 'События пользователя: тренировки, встречи, напоминания';
COMMENT ON COLUMN data.event.text IS 'Название события';
COMMENT ON COLUMN data.event.start_time IS 'Время начала';
COMMENT ON COLUMN data.event.end_time IS 'Время окончания';
COMMENT ON COLUMN data.event.date IS 'Дата события';
COMMENT ON COLUMN data.event.completed IS 'Статус выполнения';
