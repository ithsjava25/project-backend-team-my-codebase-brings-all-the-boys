import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { ThemeProvider } from '../context/ThemeContext';

// Gives context to all components (AuthContext, Router, Theme, etc.)
export function renderWithProviders(ui, options = {}) {
  const Wrapper = ({ children }) => (
    <MemoryRouter>
      <ThemeProvider>
        <AuthProvider>{children}</AuthProvider>
      </ThemeProvider>
    </MemoryRouter>
  );

  return render(ui, { wrapper: Wrapper, ...options });
}