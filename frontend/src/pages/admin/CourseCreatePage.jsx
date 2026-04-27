import {useState, useEffect, useMemo} from 'react';
import {useNavigate} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {schoolClassApi} from '@/api/schoolClasses';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
    SelectGroup,
    SelectLabel,
} from "@/components/ui/select";

export default function CourseCreatePage() {
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [classes, setClasses] = useState([]);
    const [allTeachers, setAllTeachers] = useState([]);
    const [classDetails, setClassDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [form, setForm] = useState({
        name: '',
        description: '',
        schoolClassId: '',
        leadTeacherId: '',
        endDate: ''
    });

    useEffect(() => {
        let isMounted = true;

        const fetchData = async () => {
            try {
                setLoading(true);
                const [classesData, teachersData] = await Promise.all([
                    schoolClassApi.getAllSchoolClasses(),
                    userApi.getTeachers()
                ]);

                if (isMounted) {
                    // Handle Spring Data Page object or array with null safety
                    const classesContent = classesData?.content ?? classesData;
                    setClasses(Array.isArray(classesContent) ? classesContent : []);
                    
                    setAllTeachers(teachersData || []);
                }
            } catch (err) {
                console.error('Failed to fetch classes/teachers:', err);
                if (isMounted) setError('Kunde inte ladda klasser eller lärare.');
            } finally {
                if (isMounted) setLoading(false);
            }
        };

        fetchData();

        return () => {
            isMounted = false;
        };
    }, []);

    // Fetch details for the selected class to get its enrolled teachers
    useEffect(() => {
        if (!form.schoolClassId) {
            setClassDetails(null);
            return;
        }

        let isMounted = true;
        const fetchClassDetails = async () => {
            try {
                const data = await schoolClassApi.getSchoolClassById(form.schoolClassId);
                if (isMounted) setClassDetails(data);
            } catch (err) {
                console.error('Failed to fetch class details:', err);
            }
        };

        fetchClassDetails();
        return () => { isMounted = false; };
    }, [form.schoolClassId]);

    // Split teachers into "in class" and "others"
    const groupedTeachers = useMemo(() => {
        if (!classDetails) return { inClass: [], others: allTeachers };

        const classTeacherIds = new Set(classDetails.teachers?.map(t => t.id) || []);
        
        return {
            inClass: allTeachers.filter(t => classTeacherIds.has(t.id)),
            others: allTeachers.filter(t => !classTeacherIds.has(t.id))
        };
    }, [allTeachers, classDetails]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting) return;

        if (!form.schoolClassId) {
            alert('Vänligen välj en klass.');
            return;
        }

        setIsSubmitting(true);
        try {
            const submissionData = {
                ...form,
                leadTeacherId: form.leadTeacherId === 'none' || form.leadTeacherId === '' ? null : form.leadTeacherId,
                endDate: form.endDate === '' ? null : form.endDate
            };
            
            await courseApi.createCourse(submissionData);
            window.dispatchEvent(new CustomEvent('courses-changed'));
            alert('Kursen har skapats!');
            navigate('/admin/courses');
        } catch (err) {
            console.error('Failed to create course:', err);
            alert(err.response?.data?.message || 'Kunde inte skapa kurs.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="p-8 text-center">Laddar...</div>;

    if (error) {
        return (
            <div className="p-8 max-w-2xl mx-auto">
                <Card className="border-destructive">
                    <CardHeader>
                        <CardTitle className="text-destructive text-center">Ett fel uppstod</CardTitle>
                    </CardHeader>
                    <CardContent className="text-center space-y-4">
                        <p>{error}</p>
                        <Button onClick={() => window.location.reload()}>Försök igen</Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    return (
        <div className="p-8">
            <div className="max-w-2xl mx-auto">
                <h1 className="text-3xl font-bold mb-6">Skapa Ny Kurs</h1>
                <Card>
                    <CardHeader>
                        <CardTitle>Kursinformation</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <Label htmlFor="course-name">Kursnamn</Label>
                                <Input
                                    id="course-name"
                                    required
                                    placeholder="T.ex. Matematik 1"
                                    value={form.name}
                                    onChange={e => setForm({...form, name: e.target.value})}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="course-description">Beskrivning</Label>
                                <Textarea
                                    id="course-description"
                                    placeholder="Kursbeskrivning..."
                                    className="min-h-[100px]"
                                    value={form.description}
                                    onChange={e => setForm({...form, description: e.target.value})}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="school-class">Skolklass</Label>
                                <Select 
                                    value={form.schoolClassId} 
                                    onValueChange={val => setForm({...form, schoolClassId: val, leadTeacherId: ''})}
                                >
                                    <SelectTrigger id="school-class" className="w-full">
                                        <SelectValue placeholder="Välj en klass" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {classes.map(sc => (
                                            <SelectItem key={sc.id} value={sc.id}>{sc.name}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="lead-teacher">Kursansvarig lärare</Label>
                                <Select 
                                    value={form.leadTeacherId} 
                                    onValueChange={val => setForm({...form, leadTeacherId: val})}
                                    disabled={!form.schoolClassId}
                                >
                                    <SelectTrigger id="lead-teacher" className="w-full">
                                        <SelectValue placeholder={form.schoolClassId ? "Välj en lärare" : "Välj en klass först"} />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="none">Ingen</SelectItem>
                                        
                                        {groupedTeachers.inClass.length > 0 && (
                                            <SelectGroup>
                                                <SelectLabel>Lärare i klassen</SelectLabel>
                                                {groupedTeachers.inClass.map(t => (
                                                    <SelectItem key={t.id} value={t.id}>{t.username}</SelectItem>
                                                ))}
                                            </SelectGroup>
                                        )}

                                        {groupedTeachers.others.length > 0 && (
                                            <SelectGroup>
                                                <SelectLabel>Andra lärare (Auto-enroll)</SelectLabel>
                                                {groupedTeachers.others.map(t => (
                                                    <SelectItem key={t.id} value={t.id}>{t.username}</SelectItem>
                                                ))}
                                            </SelectGroup>
                                        )}
                                    </SelectContent>
                                </Select>
                                {form.leadTeacherId && form.leadTeacherId !== 'none' && !groupedTeachers.inClass.some(t => t.id === form.leadTeacherId) && (
                                    <p className="text-xs text-amber-600 font-medium mt-1">
                                        * Vald lärare kommer att läggas till i klassen automatiskt.
                                    </p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="end-date">Slutdatum</Label>
                                <Input
                                    id="end-date"
                                    type="datetime-local"
                                    value={form.endDate}
                                    onChange={e => setForm({...form, endDate: e.target.value})}
                                />
                            </div>

                            <div className="flex justify-end gap-3 pt-4">
                                <Button 
                                    type="button" 
                                    variant="outline" 
                                    onClick={() => navigate('/admin/courses')}
                                >
                                    Avbryt
                                </Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting ? 'Skapar...' : 'Skapa Kurs'}
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
