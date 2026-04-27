import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Grid3x3, List } from 'lucide-react';
import { Grid } from '@/components/ui/grid';
import { CourseCard } from './CourseCard';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from '@/components/ui/badge';

export function CourseListView({ courses, view: initialView = 'grid', role = 'student', loading }) {
  const [view, setView] = useState(initialView);

  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Mina Kurser</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-64">
          <p className="text-muted-foreground">Laddar kurser...</p>
        </CardContent>
      </Card>
    );
  }

  if (!courses || courses.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Mina Kurser</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">Inga kurser att visa.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader className="flex flex-row justify-between">
          <div className="space-y-1">
            <CardTitle>Mina Kurser</CardTitle>
            <p className="text-sm text-muted-foreground">{courses.length} kurs(er)</p>
          </div>
          <div className="flex gap-2">
            <Button
              variant={view === 'grid' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setView('grid')}
            >
              <Grid3x3 className="h-4 w-4 mr-2" />
              Grid
            </Button>
            <Button
              variant={view === 'table' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setView('table')}
            >
              <List className="h-4 w-4 mr-2" />
              Lista
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          {view === 'grid' ? (
            <Grid cols={3}>
              {courses.map((course) => (
                <CourseCard key={course.id} course={course} />
              ))}
            </Grid>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Kurs</TableHead>
                  <TableHead>Klass</TableHead>
                  {role === 'student' && <TableHead>Status</TableHead>}
                  {role === 'teacher' && (
                    <>
                      <TableHead className="text-right">Studenter</TableHead>
                      <TableHead className="text-right">Uppgifter</TableHead>
                    </>
                  )}
                </TableRow>
              </TableHeader>
              <TableBody>
                {courses.map((course) => (
                  <TableRow key={course.id}>
                    <TableCell className="font-medium">{course.name}</TableCell>
                    <TableCell><Badge variant="outline">{course.class}</Badge></TableCell>
                    {role === 'student' && (
                      <TableCell>
                        <Badge variant="secondary" className="bg-green-100 text-green-800 hover:bg-green-100 border-green-200">
                          Aktiv
                        </Badge>
                      </TableCell>
                    )}
                    {role === 'teacher' && (
                      <>
                        <TableCell className="text-right">{course.students || 0}</TableCell>
                        <TableCell className="text-right">{course.assignments || 0}</TableCell>
                      </>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
