import { vi, describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { CompletedAssessmentsView } from './CompletedAssessmentsView';
import { userAssignmentApi } from '@/api/userAssignments';

vi.mock('@/api/userAssignments', () => ({
  userAssignmentApi: {
    getEvaluatedAssignments: vi.fn(),
  },
}));

describe('CompletedAssessmentsView', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders loading state initially', () => {
    userAssignmentApi.getEvaluatedAssignments.mockReturnValue(new Promise(() => {}));
    render(
      <BrowserRouter>
        <CompletedAssessmentsView />
      </BrowserRouter>
    );
    expect(screen.getByText(/Laddar bedömningar/i)).toBeInTheDocument();
  });

  it('renders empty state when no assessments', async () => {
    userAssignmentApi.getEvaluatedAssignments.mockResolvedValue([]);
    render(
      <BrowserRouter>
        <CompletedAssessmentsView />
      </BrowserRouter>
    );
    await waitFor(() => {
      expect(screen.getByText(/Du har inte bedömt några uppgifter än/i)).toBeInTheDocument();
    });
  });

  it('renders assessments correctly', async () => {
    const mockAssessments = [
      {
        id: '1',
        student: { id: 's1', username: 'alice' },
        assignmentId: 'a1',
        assignmentTitle: 'Assignment 1',
        grade: 'A',
        turnedInAt: '2026-04-24T10:00:00Z',
      },
    ];
    userAssignmentApi.getEvaluatedAssignments.mockResolvedValue(mockAssessments);

    render(
      <BrowserRouter>
        <CompletedAssessmentsView />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('alice')).toBeInTheDocument();
      expect(screen.getByText('Assignment 1')).toBeInTheDocument();
      expect(screen.getByText('A')).toBeInTheDocument();
    });
    
    const editLink = screen.getByRole('link', { name: /Ändra/i });
    expect(editLink).toHaveAttribute('href', '/assignments/a1/grade/s1');
  });

  it('renders fallback for missing student data', async () => {
    const mockAssessments = [
      {
        id: '1',
        student: null,
        assignmentId: 'a1',
        assignmentTitle: 'Assignment 1',
        grade: 'A',
      },
    ];
    userAssignmentApi.getEvaluatedAssignments.mockResolvedValue(mockAssessments);

    render(
      <BrowserRouter>
        <CompletedAssessmentsView />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Okänd student/i)).toBeInTheDocument();
    });
  });

  it('renders disabled edit button when assignmentId is missing', async () => {
    const mockAssessments = [
      {
        id: '1',
        student: { id: 's1', username: 'alice' },
        assignmentId: null,
        assignmentTitle: 'Assignment 1',
        grade: 'A',
      },
    ];
    userAssignmentApi.getEvaluatedAssignments.mockResolvedValue(mockAssessments);

    render(
      <BrowserRouter>
        <CompletedAssessmentsView />
      </BrowserRouter>
    );

    await waitFor(() => {
      const editButton = screen.getByRole('button', { name: /Ändra/i });
      expect(editButton).toBeDisabled();
    });
  });
});
