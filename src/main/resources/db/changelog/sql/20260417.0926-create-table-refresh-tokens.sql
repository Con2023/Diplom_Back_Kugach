CREATE TABLE IF NOT EXISTS public.refresh_tokens(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    token_hash text NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    device VARCHAR(255),
    ip VARCHAR(255),
    user_id UUID NOT NULL REFERENCES data.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON public.refresh_tokens(user_id);

COMMENT ON TABLE public.refresh_tokens IS 'Таблица для хранения refresh-токенов: управление сессиями, выход из системы, отслеживание устройств';
COMMENT ON COLUMN public.refresh_tokens.token_hash IS 'SHA-256 хэш refresh-токена';
COMMENT ON COLUMN public.refresh_tokens.created_at IS 'Дата создания токена';
COMMENT ON COLUMN public.refresh_tokens.expires_at IS 'Дата истечения токена';
COMMENT ON COLUMN public.refresh_tokens.device IS 'Устройство/браузер';
COMMENT ON COLUMN public.refresh_tokens.ip IS 'IP-адрес клиента';