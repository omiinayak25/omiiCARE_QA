import { Component, type ErrorInfo, type ReactNode } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  message?: string;
}

// Top-level error boundary so a render failure shows a recoverable screen
// instead of a blank page.
export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, message: error.message };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error('Unhandled UI error', error, info.componentStack);
  }

  render(): ReactNode {
    if (this.state.hasError) {
      return (
        <Box sx={{ p: 4, textAlign: 'center' }} data-testid="error-boundary">
          <Typography variant="h2" gutterBottom>
            Something went wrong
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 2 }}>
            {this.state.message}
          </Typography>
          <Button variant="contained" onClick={() => window.location.assign('/')}>
            Return home
          </Button>
        </Box>
      );
    }
    return this.props.children;
  }
}
