CREATE TABLE IF NOT EXISTS data.food_entries (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE,
   name VARCHAR(255) NOT NULL,
   calories INTEGER NOT NULL,
   protein INTEGER NOT NULL,
   fats INTEGER NOT NULL,
   carbs INTEGER NOT NULL,
   meal_type VARCHAR(50) NOT NULL,
   time VARCHAR(10) NOT NULL,
   date VARCHAR(10) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_food_user_id ON data.food_entries(user_id);

COMMENT ON TABLE data.food_entries IS 'Дневник питания: записи о приёмах пищи';
COMMENT ON COLUMN data.food_entries.name IS 'Название блюда/продукта';
COMMENT ON COLUMN data.food_entries.calories IS 'Калорийность (ккал)';
COMMENT ON COLUMN data.food_entries.protein IS 'Белки (граммы)';
COMMENT ON COLUMN data.food_entries.fats IS 'Жиры (граммы)';
COMMENT ON COLUMN data.food_entries.carbs IS 'Углеводы (граммы)';
COMMENT ON COLUMN data.food_entries.meal_type IS 'Тип приёма пищи';
COMMENT ON COLUMN data.food_entries.time IS 'Время приёма';
COMMENT ON COLUMN data.food_entries.date IS 'Дата';