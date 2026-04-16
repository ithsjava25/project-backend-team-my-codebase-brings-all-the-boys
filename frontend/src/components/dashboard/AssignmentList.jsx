import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export function AssignmentList({ assignments }) {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Uppgift</TableHead>
          <TableHead className="text-right">Deadline</TableHead>
          <TableHead className="text-right">Status</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {assignments?.map((assignment) => (
          <TableRow key={assignment.id}>
            <TableCell className="font-medium">{assignment.title}</TableCell>
            <TableCell className="text-right">
              <Badge variant="outline">{assignment.endDate}</Badge>
            </TableCell>
            <TableCell className="text-right">
              <Badge variant={assignment.status === 'Submitted' ? 'success' : 'default'}>
                {assignment.status}
              </Badge>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}