CREATE TABLE IF NOT EXISTS data.user_achievement (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
   achievement_id VARCHAR(255) NOT NULL,
   unlocked BOOLEAN NOT NULL DEFAULT FALSE,
   unlocked_at TIMESTAMP,
   progress INTEGER NOT NULL DEFAULT 0,
   CONSTRAINT uq_user_achievement UNIQUE (user_id, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_user_achievement_user_id ON data.user_achievement(user_id);

COMMENT ON TABLE data.user_achievement IS 'Достижения пользователя: хранит статус разблокировки и текущий прогресс';
COMMENT ON COLUMN data.user_achievement.achievement_id IS 'Идентификатор достижения';
COMMENT ON COLUMN data.user_achievement.unlocked IS 'Флаг разблокировки';
COMMENT ON COLUMN data.user_achievement.unlocked_at IS 'Дата и время разблокировки';
COMMENT ON COLUMN data.user_achievement.progress IS 'Текущее значение прогресса';