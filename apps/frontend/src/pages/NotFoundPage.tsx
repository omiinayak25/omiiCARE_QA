import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

export function NotFoundPage() {
  const { t } = useTranslation();
  return (
    <Box sx={{ p: 6, textAlign: 'center' }} data-testid="not-found">
      <Typography variant="h1" sx={{ fontSize: '3rem' }}>
        404
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        {t('common.notFound')}
      </Typography>
      <Button component={RouterLink} to="/" variant="contained">
        {t('nav.dashboard')}
      </Button>
    </Box>
  );
}
