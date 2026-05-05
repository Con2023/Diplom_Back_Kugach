CREATE TABLE IF NOT EXISTS data.user_progress (
    user_id UUID PRIMARY KEY REFERENCES data.users(id) ON DELETE CASCADE,
    water_liters REAL NOT NULL DEFAULT 0.0,
    training_streak INTEGER NOT NULL DEFAULT 0,
    last_training_date DATE,
    completed_goals INTEGER NOT NULL DEFAULT 0,
    completed_tasks INTEGER NOT NULL DEFAULT 0,
    early_workouts INTEGER NOT NULL DEFAULT 0,
    late_workouts INTEGER NOT NULL DEFAULT 0,
    strength_workouts INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE data.user_progress IS 'Агрегированные показатели для системы достижений и статистики';
COMMENT ON COLUMN data.user_progress.water_liters IS 'Всего выпито воды (литры)';
COMMENT ON COLUMN data.user_progress.training_streak IS 'Текущая серия тренировочных дней подряд';
COMMENT ON COLUMN data.user_progress.last_training_date IS 'Дата последней тренировки';
COMMENT ON COLUMN data.user_progress.completed_goals IS 'Количество завершённых целей';
COMMENT ON COLUMN data.user_progress.completed_tasks IS 'Количество выполненных задач';
COMMENT ON COLUMN data.user_progress.early_workouts IS 'Тренировки до 8:00';
COMMENT ON COLUMN data.user_progress.late_workouts IS 'Тренировки после 22:00';
COMMENT ON COLUMN data.user_progress.strength_workouts IS 'Силовые тренировки';