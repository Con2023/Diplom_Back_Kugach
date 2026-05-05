CREATE TABLE IF NOT EXISTS data.tracker (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
 name VARCHAR(255) NOT NULL,
 icon VARCHAR(10) NOT NULL,
 target INTEGER NOT NULL,
 unit VARCHAR(50) NOT NULL,
 color VARCHAR(30) NOT NULL,
 category VARCHAR(50) NOT NULL DEFAULT 'custom',
 weekly_target INTEGER
);

CREATE INDEX IF NOT EXISTS idx_tracker_user_id ON data.tracker(user_id);

COMMENT ON TABLE data.tracker IS 'Настраиваемые трекеры привычек (вода, тренировки, чтение и т.д.)';
COMMENT ON COLUMN data.tracker.name IS 'Название трекера';
COMMENT ON COLUMN data.tracker.icon IS 'Emoji-иконка';
COMMENT ON COLUMN data.tracker.target IS 'Дневная цель';
COMMENT ON COLUMN data.tracker.unit IS 'Единица измерения';
COMMENT ON COLUMN data.tracker.color IS 'Цвет трекера в интерфейсе';
COMMENT ON COLUMN data.tracker.category IS 'Категория';
COMMENT ON COLUMN data.tracker.weekly_target IS 'Недельная цель';