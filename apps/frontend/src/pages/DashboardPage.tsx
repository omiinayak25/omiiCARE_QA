import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useAuth } from '@/auth/AuthContext';

export function DashboardPage() {
  const { t } = useTranslation();
  const { user } = useAuth();

  return (
    <Box data-testid="dashboard">
      <Typography variant="h1" gutterBottom>
        {t('dashboard.welcome', { name: user?.username ?? '' })}
      </Typography>
      <Typography color="text.secondary" gutterBottom>
        {t('dashboard.tenant', { tenant: user?.tenantId ?? '-' })}
      </Typography>

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ my: 2 }}>
        <Card sx={{ flex: 1 }}>
          <CardActionArea component={RouterLink} to="/patients">
            <CardContent>
              <Typography variant="h2">{t('dashboard.patients')}</Typography>
              <Typography color="text.secondary">{t('patients.title')}</Typography>
            </CardContent>
          </CardActionArea>
        </Card>
        <Card sx={{ flex: 1 }}>
          <CardActionArea component={RouterLink} to="/appointments">
            <CardContent>
              <Typography variant="h2">{t('dashboard.appointments')}</Typography>
              <Typography color="text.secondary">{t('appointments.title')}</Typography>
            </CardContent>
          </CardActionArea>
        </Card>
      </Stack>

      <Typography variant="h2" sx={{ mt: 3, mb: 1 }}>
        {t('dashboard.roles')}
      </Typography>
      <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
        {(user?.authorities ?? []).map((authority) => (
          <Chip key={authority} label={authority} size="small" />
        ))}
      </Stack>
    </Box>
  );
}
