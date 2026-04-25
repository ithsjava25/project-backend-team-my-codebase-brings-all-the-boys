import {useState, useEffect, useMemo, useCallback} from 'react';
import {useNavigate, Link} from 'react-router-dom';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {DataTable} from '@/components/ui/data-table';
import {PlusCircle, Edit, Trash2, Search} from 'lucide-react';
import {useAuthContext} from '@/context/AuthContext';
import {Input} from '@/components/ui/input';
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select';

export default function UserManagementPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [search, setSearch] = useState('');
    const [roleFilter, setRoleFilter] = useState('all');

    const navigate = useNavigate();
    const {user: currentUser} = useAuthContext();

    const fetchUsers = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await userApi.getAllUsers({
                page,
                size,
                search,
                role: roleFilter === 'all' ? '' : roleFilter
            });
            setUsers(data.content || []);
            setTotalPages(data.totalPages || 1);
        } catch (err) {
            setError(err.response?.data?.message || 'Kunde inte hämta användare.');
        } finally {
            setLoading(false);
        }
    }, [page, size, search, roleFilter]);

    // Reset to first page when filters change.
    useEffect(() => {
        setPage(0);
    }, [search, roleFilter]);

    useEffect(() => {
        if (currentUser?.role?.name === 'ROLE_ADMIN') {
            // Debounce only text input; paginate immediately.
            const delay = search ? 300 : 0;
            const timer = setTimeout(fetchUsers, delay);
            return () => clearTimeout(timer);
        }
    }, [fetchUsers, currentUser]);

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
            cell: ({row}) => (
                <Link to={`/profile/${row.original.id}`} className="font-medium text-primary hover:underline">
                    {row.original.username}
                </Link>
            )
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
                                    aria-label={`Redigera användare ${user.username}`}
                                    title={`Redigera användare ${user.username}`}
                                    onClick={() => navigate(`/admin/users/${user.id}/edit`)}
                                >
                                    <Edit className="h-4 w-4"/>
                                </Button>

                                <Button
                                    variant="ghost"
                                    size="icon"
                                    aria-label={`Ta bort användare ${user.username}`}
                                    title={`Ta bort användare ${user.username}`}
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

                <CardContent className="space-y-4">
                    {/* Filters */}
                    <div className="flex flex-col md:flex-row gap-4 items-end">
                        <div className="flex-1 space-y-1">
                            <label htmlFor="user-search" className="text-sm font-medium">Sök användare</label>
                            <div className="relative">
                                <Search aria-hidden="true"
                                        className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground"/>
                                <Input
                                    id="user-search"
                                    placeholder="Namn eller e-post..."
                                    className="pl-8"
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="w-full md:w-48 space-y-1">
                            <span id="role-filter-label" className="text-sm font-medium">Filtrera på roll</span>
                            <Select value={roleFilter} onValueChange={setRoleFilter}>
                                <SelectTrigger aria-labelledby="role-filter-label">
                                    <SelectValue placeholder="Välj roll"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">Alla roller</SelectItem>
                                    <SelectItem value="ROLE_ADMIN">Admin</SelectItem>
                                    <SelectItem value="ROLE_TEACHER">Lärare</SelectItem>
                                    <SelectItem value="ROLE_STUDENT">Student</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

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