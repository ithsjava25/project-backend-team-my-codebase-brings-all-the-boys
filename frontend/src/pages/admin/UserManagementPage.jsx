import {useState, useEffect, useMemo} from 'react';
import {useNavigate} from 'react-router-dom';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {DataTable} from '@/components/ui/data-table';
import {PlusCircle, Edit, Trash2} from 'lucide-react';
import {useAuthContext} from '@/context/AuthContext';

export default function UserManagementPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);

    const navigate = useNavigate();
    const {user: currentUser} = useAuthContext();

    const fetchUsers = async () => {
        try {
            setLoading(true);

            const data = await userApi.getAllUsers({page, size});

            setUsers(data.content || []);
            setTotalPages(data.totalPages || 1);

        } catch (err) {
            setError(err.response?.data?.message || 'Kunde inte hämta användare.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (currentUser?.role?.name === 'ROLE_ADMIN') fetchUsers();
    }, [page, size, currentUser]);

    const handleDelete = async (user) => {
        if (window.confirm(`Är du säker på att du vill ta bort användaren "${user.username}"?`)) {
            try {
                await userApi.deleteUser(user.id);
                fetchUsers();
            } catch (error) {
                console.error('Failed to delete user:', error);
                alert('Kunde inte ta bort användaren.');
            }
        }
    };

    const handleAddUser = () => {
        navigate('/admin/users/new');
    };

    const columns = useMemo(() => [
        {
            accessorKey: "username",
            header: "Användarnamn",
        },
        {
            accessorKey: "email",
            header: "E-post",
        },
        {
            accessorKey: "role",
            header: "Roll",
            cell: ({row}) => {
                const roleName = row.original.role?.name || 'Ingen roll';

                switch (roleName) {
                    case 'ROLE_ADMIN':
                        return 'Admin';
                    case 'ROLE_TEACHER':
                        return 'Lärare';
                    case 'ROLE_STUDENT':
                        return 'Student';
                    default:
                        return roleName;
                }
            }
        },

        ...(currentUser?.role?.name === 'ROLE_ADMIN'
            ? [
                {
                    id: "actions",
                    header: "Åtgärder",
                    cell: ({row}) => {
                        const user = row.original;

                        if (currentUser?.id === user.id) {
                            return (
                                <span className="text-muted-foreground text-sm">
                    Kan ej hantera eget konto
                  </span>
                            );
                        }

                        return (
                            <div className="flex justify-end space-x-2">
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => navigate(`/admin/users/${user.id}/edit`)}
                                >
                                    <Edit className="h-4 w-4"/>
                                </Button>

                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => handleDelete(user)}
                                >
                                    <Trash2 className="h-4 w-4 text-destructive"/>
                                </Button>
                            </div>
                        );
                    },
                },
            ]
            : []),
    ], [navigate, currentUser]);

    if (currentUser?.role?.name !== 'ROLE_ADMIN') {
        return <div className="p-8">Du har inte behörighet att se den här sidan.</div>;
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold">Användaradministration</h1>

                <Button onClick={handleAddUser} className="gap-2">
                    <PlusCircle className="h-4 w-4"/>
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
                    {!loading && !error && (
                      <DataTable
                        columns={columns}
                        data={users}
                        page={page}
                        setPage={setPage}
                        pageSize={size}
                        setPageSize={setSize}
                        totalPages={totalPages}
                      />
                    )}
                </CardContent>
            </Card>
        </div>
    );
}