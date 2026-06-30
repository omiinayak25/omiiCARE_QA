import { Link as RouterLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Toolbar from '@mui/material/Toolbar';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import EventIcon from '@mui/icons-material/Event';
import LogoutIcon from '@mui/icons-material/Logout';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import { useAuth } from '@/auth/AuthContext';
import { useColorMode } from '@/theme/ColorModeContext';

const DRAWER_WIDTH = 232;

export function AppLayout() {
  const { t } = useTranslation();
  const { logout, hasAuthority } = useAuth();
  const { toggle } = useColorMode();
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { to: '/', label: t('nav.dashboard'), icon: <DashboardIcon />, authority: undefined },
    { to: '/patients', label: t('nav.patients'), icon: <PeopleIcon />, authority: 'patient:read' },
    { to: '/appointments', label: t('nav.appointments'), icon: <EventIcon />, authority: 'appointment:read' },
  ].filter((item) => !item.authority || hasAuthority(item.authority));

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            {t('app.name')}
          </Typography>
          <Tooltip title={t('nav.toggleTheme')}>
            <IconButton color="inherit" onClick={toggle} aria-label={t('nav.toggleTheme')} data-testid="toggle-theme">
              <DarkModeIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title={t('nav.logout')}>
            <IconButton color="inherit" onClick={handleLogout} aria-label={t('nav.logout')} data-testid="logout">
              <LogoutIcon />
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: DRAWER_WIDTH, boxSizing: 'border-box' },
          display: { xs: 'none', sm: 'block' },
        }}
      >
        <Toolbar />
        <List component="nav" data-testid="primary-nav">
          {navItems.map((item) => (
            <ListItemButton
              key={item.to}
              component={RouterLink}
              to={item.to}
              selected={location.pathname === item.to}
              data-testid={`nav-${item.label.toLowerCase()}`}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, p: 3, width: '100%' }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}
