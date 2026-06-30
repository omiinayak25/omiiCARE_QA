import { createTheme, type Theme } from '@mui/material/styles';

// Enterprise palette with light and dark variants. The active mode is persisted
// by the ColorModeContext (see App).
export function buildTheme(mode: 'light' | 'dark'): Theme {
  return createTheme({
    palette: {
      mode,
      primary: { main: '#0B6E4F' },
      secondary: { main: '#1565C0' },
      ...(mode === 'light'
        ? { background: { default: '#F5F7F8', paper: '#FFFFFF' } }
        : { background: { default: '#0E1513', paper: '#16201D' } }),
    },
    shape: { borderRadius: 8 },
    typography: {
      fontFamily: 'Inter, Roboto, "Helvetica Neue", Arial, sans-serif',
      h1: { fontSize: '2rem', fontWeight: 700 },
      h2: { fontSize: '1.5rem', fontWeight: 600 },
    },
    components: {
      MuiButton: { defaultProps: { disableElevation: true } },
    },
  });
}
