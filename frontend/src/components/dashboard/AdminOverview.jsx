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
import { ActivityLogView } from './ActivityLogView';

export default function AdminOverview() {
  // Hårdkodad data BYT UT
  const quickActions = [
    { id: 1, title: 'Hantera Användare', description: 'Lägg till, redigera eller ta bort användare', icon: '👥' },
    { id: 2, title: 'Skapa Kurs', description: 'Skapa en ny kurs eller uppdatera befintlig', icon: '📚' },
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

      <ActivityLogView limit={10} />
    </div>
  );
}