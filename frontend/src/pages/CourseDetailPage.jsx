import {useState, useEffect} from 'react';
import {useParams, useSearchParams} from 'react-router-dom';
import {useCourseDetail} from '@/hooks/useCourseDetail';
import {mapToCourseDetailFormat} from '@/mappers/courseMapper';
import {useAuthContext} from '@/context/AuthContext';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Badge} from '@/components/ui/badge';
import {ArrowLeft, Edit} from 'lucide-react';
import {Link, useNavigate} from 'react-router-dom';
import {AssignmentListView} from '@/components/dashboard/AssignmentListView';
import {PendingSubmissionsView} from '@/components/dashboard/PendingSubmissionsView';
import {Button} from '@/components/ui/button';

export default function CourseDetailPage() {
    const {courseId} = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const {course, error} = useCourseDetail(courseId);
    const {user} = useAuthContext();
    const navigate = useNavigate();
    const role = user?.role?.name;

    const getValidTabs = () => {
        const base = ['overview', 'assignments'];

        if (role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') {
            return [...base, 'grading', 'participants'];
        }

        return base;
    };

    const VALID_TABS = getValidTabs();

    const initialTab = searchParams.get('tab');
    const validatedTab = VALID_TABS.includes(initialTab) ? initialTab : 'overview';

    const [activeTab, setActiveTab] = useState(validatedTab);

    useEffect(() => {
        const tab = searchParams.get('tab');

        if (!VALID_TABS.includes(tab)) {
            setActiveTab('overview');
            if (tab !== null) {
                // Clean up stale/invalid tab param from the URL
                setSearchParams(prev => {
                    const next = new URLSearchParams(prev);
                    next.delete('tab');
                    return next;
                }, {replace: true});
            }
            return;
        }

        if (tab !== activeTab) {
            setActiveTab(tab);
        }
    }, [searchParams, role]);

    const handleTabChange = (value) => {
        if (!VALID_TABS.includes(value)) return;

        setActiveTab(value);

        setSearchParams(prev => {
            const next = new URLSearchParams(prev);
            if (value === 'overview') next.delete('tab');
            else next.set('tab', value);
            return next;
        });
    };

    if (error) return <div className="p-8 text-destructive">Ett fel uppstod: {error}</div>;
    if (!course) return <div className="p-8">Laddar kurs...</div>;

    const courseData = mapToCourseDetailFormat(course);

    const getTabs = () => {
        const baseTabs = [
            {value: 'overview', label: 'Översikt'},
            {value: 'assignments', label: 'Uppgifter'},
        ];

        if (role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') {
            return [
                ...baseTabs,
                {value: 'grading', label: 'Bedömning'},
                {value: 'participants', label: 'Deltagare'},
            ];
        }

        return baseTabs;
    };

    const tabs = getTabs();

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="space-y-2">
                <Link to="/dashboard"
                      className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground">
                    <ArrowLeft className="mr-2 h-4 w-4"/>
                    Tillbaka till dashboard
                </Link>

                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <h1 className="text-3xl font-bold">{courseData.name}</h1>
                        <Badge variant="secondary">{courseData.schoolClassName}</Badge>
                    </div>

                    {role === 'ROLE_ADMIN' && (
                        <Button variant="outline" onClick={() => navigate(`/admin/courses/${courseId}/edit`)}
                                className="gap-2">
                            <Edit className="h-4 w-4"/>
                            Redigera kurs
                        </Button>
                    )}
                </div>
            </div>

            {/* Tabs */}
            <Tabs value={activeTab} onValueChange={handleTabChange} className="w-full">
                <TabsList>
                    {tabs.map((tab) => (
                        <TabsTrigger key={tab.value} value={tab.value}>
                            {tab.label}
                        </TabsTrigger>
                    ))}
                </TabsList>

                {/* Översikt */}
                <TabsContent value="overview" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Om kursen</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p>{courseData.description}</p>
                        </CardContent>
                    </Card>

                    {courseData.leadTeacher && (
                        <Card className="w-60">
                            <CardHeader>
                                <CardTitle>Kursansvarig</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p>{courseData.leadTeacher.username}</p>
                                <p className="text-sm text-muted-foreground">
                                    {courseData.leadTeacher.email}
                                </p>
                            </CardContent>
                        </Card>
                    )}
                </TabsContent>

                {/* Uppgifter */}
                <TabsContent value="assignments">
                    <AssignmentListView
                        assignments={courseData.assignments}
                        title={`Uppgifter i ${courseData.name}`}
                        subtitle={`${courseData.assignments.length} uppgift(er)`}
                    />
                </TabsContent>

                {/* Lärare: Bedömning */}
                {(role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') && (
                    <TabsContent value="grading">
                        <PendingSubmissionsView courseId={courseId}/>
                    </TabsContent>
                )}

                {/* Lärare: Deltagare */}
                {(role === 'ROLE_TEACHER' || role === 'ROLE_ADMIN') && (
                    <TabsContent value="participants">
                        <Card>
                            <CardHeader>
                                <CardTitle>Deltagare</CardTitle>
                            </CardHeader>
                            <CardContent>
                                {courseData.students.length === 0 ? (
                                    <p className="text-muted-foreground">Inga studenter registrerade i denna kurs.</p>
                                ) : (
                                    <div className="space-y-4">
                                        {courseData.students.map((student) => (
                                            <div key={student.id}
                                                 className="flex items-center justify-between border-b pb-2 last:border-0">
                                                <div>
                                                    <p className="font-medium">{student.username}</p>
                                                    <p className="text-sm text-muted-foreground">{student.email}</p>
                                                </div>
                                                <Badge variant="outline">Student</Badge>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    </TabsContent>
                )}
            </Tabs>
        </div>
    );
}