CREATE TABLE IF NOT EXISTS data.tracker_progress (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   tracker_id UUID NOT NULL REFERENCES data.tracker(id) ON DELETE CASCADE,
   user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
   date VARCHAR(10) NOT NULL,
   value INTEGER NOT NULL,
   completed BOOLEAN NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tracker_progress_user_date ON data.tracker_progress(user_id, date);

COMMENT ON TABLE data.tracker_progress IS 'Ежедневный прогресс по трекерам';
COMMENT ON COLUMN data.tracker_progress.tracker_id IS 'Ссылка на трекер';
COMMENT ON COLUMN data.journal_entries.date IS 'Дата трека';
COMMENT ON COLUMN data.tracker_progress.value IS 'Введённое значение';
COMMENT ON COLUMN data.tracker_progress.completed IS 'Флаг выполнения дневной цели';