import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';

// Gives context to all components (AuthContext, Router, etc.)
export function renderWithProviders(ui, options = {}) {
  const Wrapper = ({ children }) => (
    <MemoryRouter>
      <AuthProvider>{children}</AuthProvider>
    </MemoryRouter>
  );

  return render(ui, { wrapper: Wrapper, ...options });
}