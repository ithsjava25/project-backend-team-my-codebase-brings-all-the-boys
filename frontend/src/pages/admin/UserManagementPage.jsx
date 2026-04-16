import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '@/api/users';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table } from '@/components/ui/table'; // Assuming shadcn/ui DataTable component is available
import { PlusCircle, Edit, Trash2 } from 'lucide-react';
import { useAuthContext } from '@/context/AuthContext';

// Define the columns for the DataTable
const columns = [
  {
    accessorKey: "username",
    header: "Användarnamn",
  },
  {
    accessorKey: "email",
    header: "E-post",
  },
  {
    accessorKey: "role.name",
    header: "Roll",
    cell: ({ row }) => {
      const roleName = row.original.role?.name || 'Ingen roll';
      // Basic mapping for display, could be improved
      switch (roleName) {
        case 'ROLE_ADMIN': return 'Admin';
        case 'ROLE_TEACHER': return 'Lärare';
        case 'ROLE_STUDENT': return 'Student';
        default: return roleName;
      }
    }
  },
  {
    id: "actions",
    header: "Åtgärder",
    cell: ({ row }) => {
      const user = row.original;
      const navigate = useNavigate();
      const { user: currentUser } = useAuthContext(); // To prevent self-deletion/editing

      const handleEdit = () => navigate(`/admin/users/${user.id}/edit`);
      const handleDelete = async () => {
        if (window.confirm(`Är du säker på att du vill ta bort användaren "${user.username}"?`)) {
          try {
            await userApi.deleteUser(user.id);
            alert('Användaren har tagits bort.');
            window.location.reload(); // Simple reload for now
          } catch (error) {
            console.error('Failed to delete user:', error);
            alert('Kunde inte ta bort användaren.');
          }
        }
      };

      // Do not allow admin to edit/delete themselves (or the current user)
      if (currentUser?.id === user.id) {
        return <span className="text-muted-foreground text-sm">Kan ej hantera eget konto</span>;
      }

      return (
        <div className="flex justify-end space-x-2">
          <Button variant="ghost" size="icon" onClick={handleEdit}>
            <Edit className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" onClick={handleDelete}>
            <Trash2 className="h-4 w-4 text-destructive" />
          </Button>
        </div>
      );
    },
  },
];

export default function UserManagementPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const { user } = useAuthContext();

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await userApi.getAllUsers(); // Assuming getAllUsers returns paginated data or a list
      setUsers(data.content || data); // Adjust based on actual response structure
    } catch (err) {
      setError(err.response?.data?.message || 'Kunde inte hämta användare.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleAddUser = () => {
    navigate('/admin/users/new');
  };

  // Render page only if user is admin
  if (user?.role?.name !== 'ROLE_ADMIN') {
    return <div className="p-8">Du har inte behörighet att se den här sidan.</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">Användaradministration</h1>
        <Button onClick={handleAddUser} className="gap-2">
          <PlusCircle className="h-4 w-4" />
          Ny användare
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Alla Användare</CardTitle>
        </CardHeader>
        <CardContent>
          {loading && <p>Laddar användare...</p>}
          {error && <p className="text-destructive">Fel: {error}</p>}
          {!loading && !error && users.length === 0 && <p>Inga användare hittades.</p>}
          {!loading && !error && users.length > 0 && (
            <Table columns={columns} data={users} />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
