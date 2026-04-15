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

export default function AdminOverview() {
  // Hårdkodad data BYT UT
  const quickActions = [
    { id: 1, title: 'Hantera Användare', description: 'Lägg till, redigera eller ta bort användare', icon: '👥' },
    { id: 2, title: 'Skapa Kurs', description: 'Skapa en ny kurs eller uppdatera befintlig', icon: '📚' },
  ];

  // Hårdkodad data - senaste aktivitet
  const recentActivity = [
    { id: 1, action: 'User registered', user: 'Ny Student (anna@example.com)', time: '5 min sedan' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Shield className="h-8 w-8 text-red-600" />
          <div>
            <h2 className="text-2xl font-bold">Adminpanel</h2>
          </div>
        </div>
      </div>

      <div>
        <h3 className="text-xl font-semibold mb-4">Snabbåtgärder</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {quickActions.map((action) => (
            <Card key={action.id} className="border border-border/50">
              <CardHeader>
                <div className="flex items-center gap-3">
                  <span className="text-2xl">{action.icon}</span>
                  <CardTitle className="text-lg text-muted-foreground">{action.title}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground/70">{action.description}</p>
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