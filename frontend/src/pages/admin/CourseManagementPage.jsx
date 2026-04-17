import {useState, useEffect, useMemo} from 'react';
import {useNavigate} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {DataTable} from '@/components/ui/data-table';
import {PlusCircle, Edit, Trash2} from 'lucide-react';
import {useAuthContext} from '@/context/AuthContext';

export default function CourseManagementPage() {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const navigate = useNavigate();
    const {user} = useAuthContext();

    const fetchCourses = async () => {
        try {
            setLoading(true);
            const data = await courseApi.getAllCourses();
            setCourses(data);
        } catch (err) {
            setError(err.response?.data?.message || 'Kunde inte hämta kurser.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user?.role?.name === 'ROLE_ADMIN') fetchCourses();
    }, [user]);

    const handleAddCourse = () => {
        navigate('/admin/courses/new');
    };

    const handleDelete = async (course) => {
        if (window.confirm(`Är du säker på att du vill ta bort kursen "${course.name}"?`)) {
            try {
                await courseApi.deleteCourse(course.id);
                alert('Kursen har tagits bort.');
                fetchCourses();
            } catch (error) {
                console.error('Failed to delete course:', error);
                alert('Kunde inte ta bort kursen.');
            }
        }
    };

    // ✅ FIX: columns inside useMemo (no hooks here anymore)
    const columns = useMemo(() => [
        {
            accessorKey: "name",
            header: "Kursnamn",
            cell: ({row}) => {
                const course = row.original;
                return (
                    <button
                        onClick={() => navigate(`/admin/courses/${course.id}`)}
                        className="text-primary hover:underline font-medium"
                    >
                        {course.name}
                    </button>
                );
            },
        },
        {
            accessorKey: "description",
            header: "Beskrivning",
        },
        {
            accessorKey: "schoolClassName",
            header: "Klass",
        },
        {
            accessorKey: "leadTeacher.username",
            header: "Huvudlärare",
        },
        {
            accessorKey: "endDate",
            header: "Slutdatum",
            cell: ({row}) => {
                const date = row.getValue("endDate");
                if (!date) return '-';
                return new Date(date).toLocaleDateString('sv-SE', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                });
            },
        },

        // ✅ conditionally include actions column
        ...(user?.role?.name === 'ROLE_ADMIN'
            ? [
                {
                    id: "actions",
                    header: "Åtgärder",
                    cell: ({row}) => {
                        const course = row.original;

                        return (
                            <div className="flex justify-end space-x-2">
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => navigate(`/admin/courses/${course.id}/edit`)}
                                >
                                    <Edit className="h-4 w-4"/>
                                </Button>

                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => handleDelete(course)}
                                >
                                    <Trash2 className="h-4 w-4 text-destructive"/>
                                </Button>
                            </div>
                        );
                    },
                },
            ]
            : []),
    ], [navigate, user]);

    if (user?.role?.name !== 'ROLE_ADMIN') {
        return <div className="p-8">Du har inte behörighet att se den här sidan.</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold">Kursadministration</h1>

                <Button onClick={handleAddCourse} className="gap-2">
                    <PlusCircle className="h-4 w-4"/>
                    Ny kurs
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Alla Kurser</CardTitle>
                </CardHeader>

                <CardContent>
                    {loading && <p>Laddar kurser...</p>}
                    {error && <p className="text-destructive">Fel: {error}</p>}
                    {!loading && !error && courses.length === 0 && (
                        <p>Inga kurser hittades.</p>
                    )}

                    {!loading && !error && courses.length > 0 && (
                        <DataTable
                            columns={columns}
                            data={courses}
                        />
                    )}
                </CardContent>
            </Card>
        </div>
    );
}