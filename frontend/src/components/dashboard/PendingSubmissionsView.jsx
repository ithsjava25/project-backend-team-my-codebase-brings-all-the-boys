import { useState, useEffect } from 'react';
import { dashboardApi } from '@/api/dashboard';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Link } from 'react-router-dom';
import { formatDistanceToNow } from 'date-fns';
import { sv } from 'date-fns/locale';
import { BookOpenCheck } from 'lucide-react';

export function PendingSubmissionsView({ courseId }) {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSubmissions = async () => {
      try {
        let data = await dashboardApi.getPendingSubmissions();
        if (courseId) {
          data = data.filter(s => s.courseId === courseId);
        }
        setSubmissions(data);
      } catch (error) {
        console.error('Failed to fetch pending submissions:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchSubmissions();
  }, [courseId]);

  if (loading) return <p className="text-sm text-muted-foreground p-4">Laddar inlämningar...</p>;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg flex items-center gap-2">
          <BookOpenCheck className="h-5 w-5" />
          Väntande bedömningar
        </CardTitle>
      </CardHeader>
      <CardContent>
        {submissions.length === 0 ? (
          <p className="text-sm text-muted-foreground">Inga inlämningar väntar på bedömning.</p>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Student</TableHead>
                <TableHead>Uppgift</TableHead>
                <TableHead>Kurs</TableHead>
                <TableHead>Inlämnad</TableHead>
                <TableHead className="text-right">Åtgärd</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {submissions.map((sub) => (
                <TableRow key={sub.userAssignmentId}>
                  <TableCell className="font-medium">{sub.studentName}</TableCell>
                  <TableCell>{sub.assignmentTitle}</TableCell>
                  <TableCell>{sub.courseName}</TableCell>
                  <TableCell>
                    {formatDistanceToNow(new Date(sub.submittedAt), { addSuffix: true, locale: sv })}
                  </TableCell>
                  <TableCell className="text-right">
                    <Button asChild size="sm" variant="outline">
                      <Link to={`/assignments/${sub.userAssignmentId}`}>Betygsätt</Link>
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  );
}
