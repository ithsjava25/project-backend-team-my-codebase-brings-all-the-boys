import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export default function CourseEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState(null);

    useEffect(() => {
        courseApi.getCourseById(id).then(setForm);
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        await courseApi.updateCourse(id, form);
        navigate('/admin/courses');
    };

    if (!form) return <p>Laddar...</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Redigera kurs</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">

                    <input
                        value={form.name}
                        onChange={e => setForm({...form, name: e.target.value})}
                    />

                    <textarea
                        value={form.description}
                        onChange={e => setForm({...form, description: e.target.value})}
                    />

                    <Button type="submit">Spara</Button>
                </form>
            </CardContent>
        </Card>
    );
}