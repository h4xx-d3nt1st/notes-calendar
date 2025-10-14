-- индекс + уникальность заметки в пределах дня
ALTER TABLE public.notes
  ALTER COLUMN date SET NOT NULL,
  ALTER COLUMN content SET NOT NULL,
  ALTER COLUMN index_in_day SET NOT NULL;

-- предельная длина контента уже введена в V2 (<=500)
-- гарантируем уникальность позиции в дне
CREATE UNIQUE INDEX IF NOT EXISTS ux_notes_date_idx
    ON public.notes(date, index_in_day);
