import { Badge } from '@/components/ui/badge';
import { Link } from 'react-router-dom';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export function AssignmentList({ assignments }) {
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('sv-SE');
  };

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

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Uppgift</TableHead>
          <TableHead>Inlämnad</TableHead>
          <TableHead>Bedömd</TableHead>
          <TableHead className="text-right">Deadline</TableHead>
          <TableHead className="text-right">Status</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {assignments?.map((assignment) => (
          <TableRow key={assignment.id}>
            <TableCell className="font-medium">
              <Link to={`/assignments/${assignment.id}`} className="hover:underline">
                {assignment.title}
              </Link>
            </TableCell>
            <TableCell>
                {assignment.studentStatus === 'TURNED_IN' || assignment.studentStatus === 'EVALUATED' ? (
                    <Badge variant="default" className="bg-green-500 text-[10px] h-5 px-1">Ja</Badge>
                ) : (
                    <Badge variant="secondary" className="text-[10px] h-5 px-1">Nej</Badge>
                )}
            </TableCell>
            <TableCell>
                {assignment.studentStatus === 'EVALUATED' ? (
                    <Badge variant="default" className="bg-blue-500 text-[10px] h-5 px-1">Ja</Badge>
                ) : (
                    <Badge variant="secondary" className="text-[10px] h-5 px-1">Nej</Badge>
                )}
            </TableCell>
            <TableCell className="text-right">
              <Badge variant="outline">{formatDate(assignment.deadline)}</Badge>
            </TableCell>
            <TableCell className="text-right">
              <Badge variant={getStatusVariant(assignment.status)}>
                {getStatusLabel(assignment.status)}
              </Badge>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}