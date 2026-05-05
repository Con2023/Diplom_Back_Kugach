CREATE TABLE IF NOT EXISTS public.password_reset_tokens (
    id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash  VARCHAR(512) NOT NULL UNIQUE,
    user_id     UUID NOT NULL,
    expires_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES data.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token_hash ON public.password_reset_tokens(token_hash);

COMMENT ON TABLE public.password_reset_tokens IS 'Токены для сброса пароля: одноразовые ссылки, отправляемые на email';
COMMENT ON COLUMN public.password_reset_tokens.token_hash IS 'Хэш токена сброса';
COMMENT ON COLUMN public.password_reset_tokens.expires_at IS 'Время жизни токена';