import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useAuth } from '@/auth/AuthContext';

interface FormValues {
  username: string;
  password: string;
}

export function LoginPage() {
  const { t } = useTranslation();
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { isSubmitting },
  } = useForm<FormValues>({ defaultValues: { username: '', password: '' } });

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const onSubmit = handleSubmit(async (values) => {
    setError(null);
    try {
      await login(values.username, values.password);
      navigate('/', { replace: true });
    } catch {
      setError(t('login.error'));
    }
  });

  return (
    <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', p: 2 }}>
      <Card sx={{ width: 380, maxWidth: '100%' }}>
        <CardContent>
          <Typography variant="h1" sx={{ fontSize: '1.6rem', mb: 0.5 }}>
            {t('app.name')}
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 3 }}>
            {t('app.tagline')}
          </Typography>
          <form onSubmit={onSubmit} noValidate>
            <Stack spacing={2}>
              {error && (
                <Alert severity="error" data-testid="login-error">
                  {error}
                </Alert>
              )}
              <TextField
                label={t('login.username')}
                autoComplete="username"
                inputProps={{ 'data-testid': 'login-username' }}
                {...register('username', { required: true })}
              />
              <TextField
                label={t('login.password')}
                type="password"
                autoComplete="current-password"
                inputProps={{ 'data-testid': 'login-password' }}
                {...register('password', { required: true })}
              />
              <Button
                type="submit"
                variant="contained"
                size="large"
                disabled={isSubmitting}
                data-testid="login-submit"
              >
                {t('login.submit')}
              </Button>
              <Typography variant="caption" color="text.secondary">
                {t('login.demoHint')}
              </Typography>
            </Stack>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
