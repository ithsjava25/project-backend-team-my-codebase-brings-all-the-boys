import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Grid3x3, ChevronDown } from 'lucide-react';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from '@/components/ui/badge';

export function TableView({ notStartedCourses, activeCourses, completedCourses, onViewChange }) {
  return (
    <div className="space-y-4">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div className="space-y-1">
            <CardTitle>Mina Kurser</CardTitle>
            <p className="text-sm text-muted-foreground">{activeCourses.length} pågående</p>
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => onViewChange('grid')}
          >
            <Grid3x3 className="h-4 w-4 mr-2" />
            Grid
          </Button>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Kurs</TableHead>
                <TableHead>Klass</TableHead>
                <TableHead>Progress</TableHead>
                <TableHead className="text-right">Uppgifter</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {activeCourses.map((course) => (
                <TableRow key={course.id}>
                  <TableCell className="font-medium">{course.name}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <Badge variant="green">Aktiv</Badge>
                      <Badge variant="outline">{course.class}</Badge>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <div className="w-full bg-muted rounded-full h-2 max-w-25">
                        <div
                          className="bg-primary h-2 rounded-full transition-all"
                          style={{ width: `${course.progress}%` }}
                        />
                      </div>
                      <span className="text-xs text-muted-foreground">{course.progress}%</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-right">{course.assignments}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {notStartedCourses.length > 0 && (
        <Collapsible className="group/collapsible">
          <Card className="cursor-pointer">
            <CollapsibleTrigger asChild>
              <CardHeader className="flex flex-row items-center justify-between">
                <div>
                  <CardTitle>Ej påbörjade kurser</CardTitle>
                  <p className="text-sm text-muted-foreground">{notStartedCourses.length} ej påbörjad</p>
                </div>
                <ChevronDown className="h-4 w-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-180" />
              </CardHeader>
            </CollapsibleTrigger>
            <CollapsibleContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Kurs</TableHead>
                    <TableHead>Klass</TableHead>
                    <TableHead>Progress</TableHead>
                    <TableHead className="text-right">Uppgifter</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {notStartedCourses.map((course) => (
                    <TableRow key={course.id}>
                      <TableCell className="font-medium">{course.name}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Badge variant="yellow">Ej påbörjade</Badge>
                          <Badge variant="outline">{course.class}</Badge>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <div className="w-full bg-muted rounded-full h-2 max-w-25">
                            <div
                              className="bg-primary h-2 rounded-full transition-all"
                              style={{ width: `${course.progress}%` }}
                            />
                          </div>
                          <span className="text-xs text-muted-foreground">{course.progress}%</span>
                        </div>
                      </TableCell>
                      <TableCell className="text-right">{course.assignments}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CollapsibleContent>
          </Card>
        </Collapsible>
      )}

      {completedCourses.length > 0 && (
        <Collapsible className="group/collapsible">
          <Card className="cursor-pointer">
            <CollapsibleTrigger asChild>
              <CardHeader className="flex flex-row items-center justify-between">
                <div>
                  <CardTitle>Avslutade kurser</CardTitle>
                  <p className="text-sm text-muted-foreground">{completedCourses.length} avslutad</p>
                </div>
                <ChevronDown className="h-4 w-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-180" />
              </CardHeader>
            </CollapsibleTrigger>
            <CollapsibleContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Kurs</TableHead>
                    <TableHead>Klass</TableHead>
                    <TableHead>Progress</TableHead>
                    <TableHead className="text-right">Uppgifter</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {completedCourses.map((course) => (
                    <TableRow key={course.id}>
                      <TableCell className="font-medium">{course.name}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Badge variant="outline">Avslutad</Badge>
                          <Badge variant="outline">{course.class}</Badge>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <div className="w-full bg-muted rounded-full h-2 max-w-25">
                            <div
                              className="bg-primary h-2 rounded-full transition-all"
                              style={{ width: `${course.progress}%` }}
                            />
                          </div>
                          <span className="text-xs text-muted-foreground">{course.progress}%</span>
                        </div>
                      </TableCell>
                      <TableCell className="text-right">{course.assignments}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CollapsibleContent>
          </Card>
        </Collapsible>
      )}
    </div>
  );
}
