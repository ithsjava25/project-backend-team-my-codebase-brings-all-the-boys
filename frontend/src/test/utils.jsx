import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider, TestAuthProvider } from '../context/AuthContext';
import { ThemeProvider } from '../context/ThemeContext';

export function renderWithProviders(ui, options = {}) {
  const { auth, ...renderOptions } = options;

  const AuthProviderWrapper = auth
    ? ({ children }) => (
        <MemoryRouter>
          <ThemeProvider>
            <TestAuthProvider value={auth}>{children}</TestAuthProvider>
          </ThemeProvider>
        </MemoryRouter>
      )
    : ({ children }) => (
        <MemoryRouter>
          <ThemeProvider>
            <AuthProvider>{children}</AuthProvider>
          </ThemeProvider>
        </MemoryRouter>
      );

  return render(ui, { wrapper: AuthProviderWrapper, ...renderOptions });
}