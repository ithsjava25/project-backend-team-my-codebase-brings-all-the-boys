import {useState, useEffect, useMemo, useCallback} from 'react';
import {useNavigate} from 'react-router-dom';
import {schoolClassApi} from '@/api/schoolClasses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {DataTable} from '@/components/ui/data-table';
import {PlusCircle, Edit, Trash2} from 'lucide-react';
import {useAuthContext} from '@/context/AuthContext';

export default function SchoolClassManagementPage() {
    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const navigate = useNavigate();
    const {user} = useAuthContext();

    const fetchClasses = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await schoolClassApi.getAllSchoolClasses();
            setClasses(data || []);
        } catch (err) {
            setError(err.response?.data?.message || 'Kunde inte hämta klasser.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (user?.role?.name === 'ROLE_ADMIN') fetchClasses();
    }, [user, fetchClasses]);

    const handleAddClass = () => {
        // For simplicity, we could have a dialog or a new page. 
        // Let's assume there's a create page similar to courses.
        navigate('/admin/school-classes/new');
    };

    const handleDelete = useCallback(async (sc) => {
        if (window.confirm(`Är du säker på att du vill ta bort klassen "${sc.name}"?`)) {
            try {
                await schoolClassApi.deleteSchoolClass(sc.id);
                alert('Klassen har tagits bort.');
                await fetchClasses();
            } catch (error) {
                console.error('Failed to delete class:', error);
                alert('Kunde inte ta bort klassen. Se till att den inte har några kurser eller elever kopplade.');
            }
        }
    }, [fetchClasses]);

    const columns = useMemo(() => [
        {
            accessorKey: "name",
            header: "Klassnamn",
            cell: ({row}) => {
                const sc = row.original;
                return (
                    <button
                        onClick={() => navigate(`/school-classes/${sc.id}`)}
                        className="text-primary hover:underline font-medium"
                    >
                        {sc.name}
                    </button>
                );
            },
        },
        {
            accessorKey: "description",
            header: "Beskrivning",
        },
        ...(user?.role?.name === 'ROLE_ADMIN'
            ? [
                {
                    id: "actions",
                    header: "Åtgärder",
                    cell: ({row}) => {
                        const sc = row.original;

                        return (
                            <div className="flex justify-end space-x-2">
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => navigate(`/admin/school-classes/${sc.id}/edit`)}
                                >
                                    <Edit className="h-4 w-4"/>
                                </Button>

                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => handleDelete(sc)}
                                >
                                    <Trash2 className="h-4 w-4 text-destructive"/>
                                </Button>
                            </div>
                        );
                    },
                },
            ]
            : []),
    ], [navigate, user?.role?.name, handleDelete]);

    if (user?.role?.name !== 'ROLE_ADMIN') {
        return <div className="p-8">Du har inte behörighet att se den här sidan.</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold">Klassadministration</h1>

                <Button onClick={handleAddClass} className="gap-2">
                    <PlusCircle className="h-4 w-4"/>
                    Ny klass
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Alla Klasser</CardTitle>
                </CardHeader>

                <CardContent>
                    {loading && <p>Laddar klasser...</p>}
                    {error && <p className="text-destructive">Fel: {error}</p>}
                    {!loading && !error && (
                        <DataTable
                            columns={columns}
                            data={classes}
                            // Assuming DataTable handles arrays too if pagination props are missing or adapted
                            page={0}
                            setPage={() => {}}
                            pageSize={classes.length}
                            setPageSize={() => {}}
                            totalPages={1}
                        />
                    )}
                </CardContent>
            </Card>
        </div>
    );
}