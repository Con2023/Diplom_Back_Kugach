CREATE TABLE IF NOT EXISTS data.journal_entries (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
  type VARCHAR(50) NOT NULL,
  date VARCHAR(10) NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  mood VARCHAR(50) NOT NULL,
  tags TEXT
);

CREATE INDEX IF NOT EXISTS idx_journal_user_id ON data.journal_entries(user_id);
CREATE INDEX IF NOT EXISTS idx_journal_date ON data.journal_entries(date);

COMMENT ON TABLE data.journal_entries IS 'Записи дневника: эмоции, мысли, благодарность, сны';
COMMENT ON COLUMN data.journal_entries.type IS 'Тип записи';
COMMENT ON COLUMN data.journal_entries.date IS 'Дата записи';
COMMENT ON COLUMN data.journal_entries.title IS 'Заголовок записи';
COMMENT ON COLUMN data.journal_entries.content IS 'Основное содержание';
COMMENT ON COLUMN data.journal_entries.mood IS 'Настроение';
COMMENT ON COLUMN data.journal_entries.tags IS 'Теги';