import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {BookIcon, Shield, UserRoundIcon} from 'lucide-react';
import { ActivityLogView } from './ActivityLogView';
import { useNavigate } from 'react-router-dom';

export default function AdminOverview() {
  const navigate = useNavigate();
  const quickActions = [
    {
      id: 1,
      title: 'Hantera Användare',
      description: 'Lägg till, redigera eller ta bort användare',
      icon: <UserRoundIcon/>,
      path: '/admin/users'
    },
    {
      id: 2,
      title: 'Hantera Kurser',
      description: 'Skapa nya eller uppdatera befintliga kurser',
      icon: <BookIcon/>,
      path: '/admin/courses'
    },
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
            <Card key={action.id} className="border border-border/50 hover:cursor-pointer hover:bg-neutral-800/50" onClick={() => navigate(action.path)}>
              <CardHeader>
                <div className="flex items-center gap-3">
                  <span className="text-xl">{action.icon}</span>
                  <CardTitle>{action.title}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">{action.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      <ActivityLogView limit={10} />
    </div>
  );
}