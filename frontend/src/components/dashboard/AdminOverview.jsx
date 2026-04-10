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
import { Shield } from 'lucide-react';
import { Button } from '@/components/ui/button';

export default function AdminOverview() {
  // Hårdkodad data BYT UT
  const quickActions = [
    { id: 1, title: 'Hantera Användare', description: 'Lägg till, redigera eller ta bort användare', icon: '👥' },
    { id: 2, title: 'Skapa Kurs', description: 'Skapa en ny kurs eller uppdatera befintlig', icon: '📚' },
    { id: 3, title: 'Systeminställningar', description: 'Konfigurera systemet', icon: '⚙️' },
  ];

  // Hårdkodad data - senaste aktivitet
  const recentActivity = [
    { id: 1, action: 'User registered', user: 'Ny Student (anna@example.com)', time: '5 min sedan' },
    { id: 2, action: 'Assignment created', user: 'Teacher: Erik (erik@example.com)', time: '15 min sedan' },
    { id: 3, action: 'Course updated', user: 'Admin: You', time: '1 timme sedan' },
    { id: 4, action: 'User role changed', user: 'Student → Teacher', time: '2 timmar sedan' },
    { id: 5, action: 'Assignment graded', user: 'Teacher: Lisa', time: '3 timmar sedan' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Shield className="h-8 w-8 text-red-600" />
          <div>
            <h2 className="text-2xl font-bold">Admin Panel</h2>
            <p className="text-sm text-muted-foreground">Systemadministrationsvy</p>
          </div>
        </div>
        <Button>Gå till Admin</Button>
      </div>

      <div>
        <h3 className="text-xl font-semibold mb-4">Snabbåtgärder</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {quickActions.map((action) => (
            <Card key={action.id} className="hover:bg-muted/50 transition-colors cursor-pointer">
              <CardHeader>
                <div className="flex items-center gap-3">
                  <span className="text-2xl">{action.icon}</span>
                  <CardTitle className="text-lg">{action.title}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">{action.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Senaste Aktivitet</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Aktivitet</TableHead>
                <TableHead>Användare</TableHead>
                <TableHead className="text-right">Tid</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {recentActivity.map((activity) => (
                <TableRow key={activity.id}>
                  <TableCell className="font-medium">{activity.action}</TableCell>
                  <TableCell className="text-muted-foreground">{activity.user}</TableCell>
                  <TableCell className="text-right">
                    <Badge variant="outline">{activity.time}</Badge>
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