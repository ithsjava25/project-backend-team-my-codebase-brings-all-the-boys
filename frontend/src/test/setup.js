import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import * as matchers from '@testing-library/jest-dom';

// Extend 'expect' with jest-dom matchers
expect.extend(matchers);

afterEach(() => {
  cleanup();
});