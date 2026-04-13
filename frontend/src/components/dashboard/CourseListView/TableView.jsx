import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Grid3x3, ChevronDown, StarIcon } from 'lucide-react';
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
import { DayProgress } from '../DayProgress';

export function TableView({ notStartedCourses, activeCourses, completedCourses, onViewChange }) {
  return (
    <div className="space-y-4">
      <Card>
        <CardHeader className="flex flex-row justify-between">
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
                  <TableHead style={{ width: '10%' }} className="text-left">Kurs</TableHead>
                  <TableHead style={{ width: '10%' }} className="text-center">Status</TableHead>
                  <TableHead style={{ width: '10%' }} className="text-center">Klass</TableHead>
                  <TableHead style={{ width: '30%' }} className="text-center">Tidslinje</TableHead>
                  <TableHead style={{ width: '12%' }} className="text-center">Moment</TableHead>
                  <TableHead style={{ width: '0%' }} className="text-right">Favorit</TableHead>
                </TableRow>
              </TableHeader>
            <TableBody>
              {activeCourses.map((course) => (
                <TableRow key={course.id}>
                  <TableCell className="font-medium text-left">{course.name}</TableCell>
                  <TableCell className="font-medium text-center">
                    <Badge variant="purple">Pågående</Badge>
                  </TableCell>
                  <TableCell className="font-medium text-center">
                    <Badge variant="outline">{course.class}</Badge>
                  </TableCell>
                  <TableCell className="font-medium text-center">
                    <DayProgress course={course} />
                  </TableCell>
                  <TableCell className="font-medium text-center">{course.completed ?? 0}/{course.assignments}</TableCell>
                  <TableCell className="font-medium items-right text-right">
                    {course.isFavorite && <StarIcon className="h-4 w-4 text-yellow-500 fill-yellow-500" />}
                  </TableCell>
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
                    <TableHead style={{ width: '20%' }}>Kurs</TableHead>
                    <TableHead style={{ width: '12%' }}>Status</TableHead>
                    <TableHead style={{ width: '8%' }}>Klass</TableHead>
                    <TableHead style={{ width: '30%' }}>Tidslinje</TableHead>
                    <TableHead style={{ width: '12%' }} className="text-right">Moment</TableHead>
                    <TableHead style={{ width: '10%' }} className="text-center">Favorit</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {notStartedCourses.map((course) => (
                    <TableRow key={course.id}>
                  <TableCell className="font-medium">{course.name}</TableCell>
                  <TableCell>
                    <Badge variant="yellow">Ej påbörjad</Badge>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{course.class}</Badge>
                  </TableCell>
                  <TableCell>
                    <DayProgress course={course} />
                  </TableCell>
                  <TableCell>{course.completed ?? 0}/{course.assignments}</TableCell>
                  <TableCell className="text-center">
                    {course.isFavorite && <StarIcon className="h-4 w-4 text-yellow-500 fill-yellow-500" />}
                  </TableCell>
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
                    <TableHead style={{ width: '20%' }}>Kurs</TableHead>
                    <TableHead style={{ width: '12%' }}>Status</TableHead>
                    <TableHead style={{ width: '8%' }}>Klass</TableHead>
                    <TableHead style={{ width: '30%' }}>Tidslinje</TableHead>
                    <TableHead style={{ width: '12%' }} className="text-right">Moment</TableHead>
                    <TableHead style={{ width: '10%' }} className="text-center">Favorit</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {completedCourses.map((course) => (
                    <TableRow key={course.id}>
                  <TableCell className="font-medium">{course.name}</TableCell>
                  <TableCell>
                    <Badge variant="secondary">Avslutad</Badge>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{course.class}</Badge>
                  </TableCell>
                  <TableCell>
                    <DayProgress course={course} />
                  </TableCell>
                  <TableCell className="text-right">{course.completed ?? 0}/{course.assignments}</TableCell>
                  <TableCell className="text-center">
                    {course.isFavorite && <StarIcon className="h-4 w-4 text-yellow-500 fill-yellow-500" />}
                  </TableCell>
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
