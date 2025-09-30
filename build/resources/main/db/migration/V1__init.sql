CREATE TABLE IF NOT EXISTS public.notes (
  id            BIGSERIAL PRIMARY KEY,
  date          DATE        NOT NULL,
  content       TEXT        NOT NULL,
  index_in_day  INTEGER     NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_notes_date_index_i
  ON public.notes(date, index_in_day);

CREATE INDEX IF NOT EXISTS idx_notes_date
  ON public.notes(date);
