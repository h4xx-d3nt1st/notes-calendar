-- индекс + уникальность заметки в пределах дня (H2/PostgreSQL friendly)

-- NOT NULL по отдельности (H2 не понимает через запятую)
ALTER TABLE notes ALTER COLUMN date SET NOT NULL;
ALTER TABLE notes ALTER COLUMN content SET NOT NULL;
ALTER TABLE notes ALTER COLUMN index_in_day SET NOT NULL;

-- Уникальность пары (date, index_in_day)
-- Вариант через constraint (подходит и для H2, и для Postgres)
ALTER TABLE notes
  ADD CONSTRAINT uq_notes_date_index UNIQUE (date, index_in_day);
