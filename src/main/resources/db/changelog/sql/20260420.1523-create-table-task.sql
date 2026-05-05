CREATE TABLE IF NOT EXISTS data.task (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
  text TEXT NOT NULL,
  goal_id UUID NULL,
  description TEXT,
  date TIMESTAMP,
  completed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON data.task(user_id);

COMMENT ON TABLE data.task IS 'Задачи пользователя: могут быть связаны с целями (goal_id), имеют статус выполнения';
COMMENT ON COLUMN data.task.text IS 'Название задачи';
COMMENT ON COLUMN data.task.goal_id IS 'Связь с таблицей целей';
COMMENT ON COLUMN data.task.description IS 'Подробное описание';
COMMENT ON COLUMN data.task.date IS 'Дата, на которую запланирована задача';
COMMENT ON COLUMN data.task.completed IS 'Статус выполнения';