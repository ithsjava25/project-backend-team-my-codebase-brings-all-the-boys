import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from '@/components/ui/badge';
import { Link } from 'react-router-dom';

export function AssignmentListView({
  assignments,
  title = "Uppgifter",
  subtitle,
  error,
  loading,
  emptyMessage = "Inga uppgifter än."
}) {
  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-64">
          <p className="text-muted-foreground" role="status" aria-live="polite">Laddar uppgifter...</p>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardContent className="flex items-center justify-center h-64">
          <p className="text-destructive">Fel: {error}</p>
        </CardContent>
      </Card>
    );
  }

  if (!Array.isArray(assignments) || assignments.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">{emptyMessage}</p>
        </CardContent>
      </Card>
    );
  }

  const getStatusVariant = (status) => {
    switch (status) {
      case 'OPEN': return 'default';
      case 'CLOSED': return 'secondary';
      case 'CREATED': return 'outline';
      default: return 'outline';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'OPEN': return 'Öppen';
      case 'CLOSED': return 'Stängd';
      case 'CREATED': return 'Skapad';
      default: return status;
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('sv-SE');
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        {subtitle && (
          <p className="text-sm text-muted-foreground">{subtitle}</p>
        )}
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Uppgift</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Inlämnad</TableHead>
              <TableHead>Bedömd</TableHead>
              <TableHead>Slutdatum</TableHead>
              <TableHead className="text-right">Skapad</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {assignments.map((assignment) => (
              <TableRow key={assignment.id}>
                <TableCell className="font-medium">
                  <Link to={`/assignments/${assignment.id}`} className="hover:underline">
                    {assignment.title}
                  </Link>
                </TableCell>
                <TableCell>
                  <Badge variant={getStatusVariant(assignment.status)}>
                    {getStatusLabel(assignment.status)}
                  </Badge>
                </TableCell>
                <TableCell>
                    {assignment.studentStatus === 'TURNED_IN' || assignment.studentStatus === 'EVALUATED' ? (
                        <Badge variant="default" className="bg-green-500">Ja</Badge>
                    ) : (
                        <Badge variant="secondary">Nej</Badge>
                    )}
                </TableCell>
                <TableCell>
                    {assignment.studentStatus === 'EVALUATED' ? (
                        <Badge variant="default" className="bg-blue-500">Ja</Badge>
                    ) : (
                        <Badge variant="secondary">Nej</Badge>
                    )}
                </TableCell>
                <TableCell>{formatDate(assignment.deadline)}</TableCell>
                <TableCell className="text-right">{formatDate(assignment.createdAt)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
