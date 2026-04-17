import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export default function CourseCreatePage() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: '',
        description: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        await courseApi.createCourse(form);
        navigate('/admin/courses');
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Skapa kurs</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">

                    <input
                        placeholder="Kursnamn"
                        value={form.name}
                        onChange={e => setForm({...form, name: e.target.value})}
                    />

                    <textarea
                        placeholder="Beskrivning"
                        value={form.description}
                        onChange={e => setForm({...form, description: e.target.value})}
                    />

                    <Button type="submit">Skapa</Button>
                </form>
            </CardContent>
        </Card>
    );
}