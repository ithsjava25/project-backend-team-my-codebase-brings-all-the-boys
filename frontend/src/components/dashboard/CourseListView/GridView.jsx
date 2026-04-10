import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { List, ChevronDown } from 'lucide-react';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { CourseCard } from '../CourseCard';
import { Grid } from '@/components/ui/grid';

export function GridView({ notStartedCourses, activeCourses, completedCourses, onViewChange }) {
  return (
    <>
      <Card className="mb-6">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>
            Pågående kurser
            <p className="text-sm text-muted-foreground">{activeCourses.length} pågående</p>
          </CardTitle>
          <Button variant="outline" size="sm" onClick={() => onViewChange('table')}>
            <List className="h-4 w-4 mr-2" />
            Table
          </Button>
        </CardHeader>
        <CardContent>
          <Grid cols={3}>
            {activeCourses.map((course) => (
              <CourseCard key={course.id} course={course} status="active" />
            ))}
          </Grid>
        </CardContent>
      </Card>

      {notStartedCourses.length > 0 && (
        <Collapsible className="group/collapsible mb-6">
          <Card className="cursor-pointer p-6">
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
              <Grid cols={3}>
                {notStartedCourses.map((course) => (
                  <CourseCard key={course.id} course={course} status="not-started" />
                ))}
              </Grid>
            </CollapsibleContent>
          </Card>
        </Collapsible>
      )}

      {completedCourses.length > 0 && (
        <Collapsible className="group/collapsible">
          <Card className="cursor-pointer p-6">
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
                <Grid cols={3}>
                  {completedCourses.map((course) => (
                    <CourseCard key={course.id} course={course} status="completed" />
                  ))}
                </Grid>
            </CollapsibleContent>
          </Card>
        </Collapsible>
      )}
    </>
  );
}
