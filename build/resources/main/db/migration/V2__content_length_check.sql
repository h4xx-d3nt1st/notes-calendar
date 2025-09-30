-- Ограничение длины контента: максимум 500 символов
ALTER TABLE public.notes
  ADD CONSTRAINT notes_content_len CHECK (char_length(content) <= 500);
