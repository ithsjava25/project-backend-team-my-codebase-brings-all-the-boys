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
import { Button } from '@/components/ui/button';

export default function TeacherOverview() {
  // Hårdkodad data BYT UT
  const myCourses = [
    { id: 1, name: 'Java Backend 1', class: 'TE24A', students: 28, assignments: 5 },
    { id: 2, name: 'Webbutveckling', class: 'TE24C', students: 32, assignments: 4 },
  ];

  // Hårdkodad data - uppgifter som behöver rättas
  const pendingGrading = [
    { id: 1, title: 'Inlämning: Java Streams', student: 'Anna Andersson', course: 'Java Backend 1', submitted: '2026-04-10', status: 'Väntar' },
    { id: 2, title: 'Projekt: React Components', student: 'Erik Eriksson', course: 'Webbutveckling', submitted: '2026-04-09', status: 'Väntar' },
    { id: 3, title: 'Quiz: Databasteknik', student: 'Sara Svensson', course: 'Databasteknik', submitted: '2026-04-11', status: 'Graderad' },
  ];

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Mina Kurser</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Kurs</TableHead>
                <TableHead>Klass</TableHead>
                <TableHead className="text-right">Studenter</TableHead>
                <TableHead className="text-right">Uppgifter</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {myCourses.map((course) => (
                <TableRow key={course.id}>
                  <TableCell className="font-medium">{course.name}</TableCell>
                  <TableCell><Badge variant="outline">{course.class}</Badge></TableCell>
                  <TableCell className="text-right">{course.students}</TableCell>
                  <TableCell className="text-right">{course.assignments}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <div className="space-y-1">
            <CardTitle>Ej Rättade Inlämningar</CardTitle>
            <p className="text-sm text-muted-foreground">Uppgifter som väntar på din bedömning</p>
          </div>
          <Badge variant="destructive">{pendingGrading.filter(p => p.status === 'Väntar').length} st</Badge>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Uppgift</TableHead>
                <TableHead>Student</TableHead>
                <TableHead>Kurs</TableHead>
                <TableHead>Inlämnad</TableHead>
                <TableHead className="text-right">Status</TableHead>
                <TableHead className="text-right">Åtgärd</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {pendingGrading.map((item) => (
                <TableRow key={item.id}>
                  <TableCell className="font-medium">{item.title}</TableCell>
                  <TableCell>{item.student}</TableCell>
                  <TableCell><Badge variant="outline">{item.course}</Badge></TableCell>
                  <TableCell>{item.submitted}</TableCell>
                  <TableCell className="text-right">
                    <Badge variant={item.status === 'Väntar' ? 'destructive' : 'success'}>
                      {item.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <Button size="sm" variant="ghost">Rätta</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}