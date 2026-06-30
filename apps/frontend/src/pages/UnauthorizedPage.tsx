import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

export function UnauthorizedPage() {
  const { t } = useTranslation();
  return (
    <Box sx={{ p: 6, textAlign: 'center' }} data-testid="unauthorized">
      <Typography variant="h1" sx={{ fontSize: '3rem' }}>
        403
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        {t('common.unauthorized')}
      </Typography>
      <Button component={RouterLink} to="/" variant="contained">
        {t('nav.dashboard')}
      </Button>
    </Box>
  );
}
