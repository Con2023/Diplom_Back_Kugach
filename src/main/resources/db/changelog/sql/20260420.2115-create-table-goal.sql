CREATE TABLE IF NOT EXISTS data.goal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    category VARCHAR(50),
    priority VARCHAR(20),
    deadline VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_goals_user_id ON data.goal(user_id);

COMMENT ON TABLE data.goal IS 'Цели пользователя: долгосрочные планы, категории, приоритеты';
COMMENT ON COLUMN data.goal.text IS 'Название цели';
COMMENT ON COLUMN data.goal.category IS 'Категория';
COMMENT ON COLUMN data.goal.priority IS 'Приоритет';
COMMENT ON COLUMN data.goal.deadline IS 'Дедлайн';
COMMENT ON COLUMN data.goal.created_at IS 'Дата создания цели';
