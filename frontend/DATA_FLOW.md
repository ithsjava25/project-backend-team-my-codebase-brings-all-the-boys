# Dataflöde: API → Visning

Denna dokumentation förklarar hur data flödar från backend API genom frontend-arkitekturen till visning i UI.

---

## 📊 Översikt

```
Backend API
    ↓
API Clients (api/)
    ↓
Hooks (hooks/)
    ↓
Mappers (mappers/) [valfritt]
    ↓
Components (components/)
    ↓
UI (Pages)
```

---

## 1. Backend API

Alla API-anrop går mot `/api` bas-URL.

### Kurs-endpoints
```
GET  /api/courses           → Hämta alla kurser (grundinfo)
GET  /api/courses/{id}      → Hämta specifik kurs (med uppdrag)
```

### Uppdrag-endpoints
```
GET  /api/assignments           → Hämta alla uppdrag (filtrerat på användare)
GET  /api/assignments/{id}      → Hämta specifikt uppdrag
GET  /api/assignments/my-created → Hämta uppdrag skapade av mig (lärare)
```

---

## 2. API Clients

Ligger i `frontend/src/api/`. Dessa filer hanterar kommunikationen med backend.

### `api/client.js`
Base Axios-konfiguration med:
- `baseURL: '/api'`
- `withCredentials: true` (session cookie)
- Standard headers

### `api/courses.js`
```javascript
export const courseApi = {
  getAllCourses: async () => {
    const response = await client.get('/courses');
    return response.data;  // Array av courses
  },

  getCourseById: async (id) => {
    const response = await client.get(`/courses/${id}`);
    return response.data;  // En course med assignments
  },
};
```

### `api/assignments.js`
```javascript
export const assignmentApi = {
  getAllAssignments: async () => {
    const response = await client.get('/assignments');
    return response.data;  // Array av assignments
  },

  getAssignmentById: async (id) => {
    const response = await client.get(`/assignments/${id}`);
    return response.data;  // Ett assignment
  },
};
```

---

## 3. Hooks

Ligger i `frontend/src/hooks/`. Dessa hanterar datahämtning, loading och error states.

### `hooks/useCourses.js`
```javascript
export function useCourses() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const data = await courseApi.getAllCourses();
        setCourses(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch courses');
      } finally {
        setLoading(false);
      }
    };

    void fetchCourses();
  }, []);

  return { courses, loading, error };
}
```

**Användning:**
```javascript
const { courses, loading, error } = useCourses();
```

### `hooks/useCourseDetail.js`
```javascript
export function useCourseDetail(courseId) {
  // Samma struktur som useCourses men för en kurs
  // Hämtar kurs med uppdrag

  return { course, loading, error };
}
```

### `hooks/useAssignments.js`
```javascript
export function useAssignments() {
  // Samma struktur som useCourses men för uppdrag

  return { assignments, loading, error };
}
```

---

## 4. Mappers

Ligger i `frontend/src/mappers/`. Dessa transformerar data från backend till frontend-format.

### `mappers/courseMapper.js`

#### `mapToCardFormat(courses)`
Transformerar kursdata förkortad visning (cards, grid):

```javascript
// Input: Array från API
[
  {
    id: "uuid-1",
    name: "Java Backend 1",
    description: "...",
    schoolClass: { name: "TE24A" },
    leadTeacher: { username: "lars", email: "lars@example.com" }
  }
]

// Output:
[
  {
    id: "uuid-1",
    name: "Java Backend 1",
    class: "TE24A",           // ← transformerat från schoolClass.name
    teacher: "lars",           // ← transformerat från leadTeacher.username
    assignments: 5,
    progress: 60
  }
]
```

#### `mapToCourseDetailFormat(course)`
Transformerar kursdata för detaljvy:

```javascript
// Input: Course object från API
{
  id: "uuid-1",
  name: "Java Backend 1",
  description: "...",
  schoolClass: { name: "TE24A" },
  leadTeacher: { username: "lars", email: "lars@example.com" },
  assignments: [
    { id: "a1", title: "Inlämning 1", status: "OPEN", createdAt: "2026-01-01" }
  ]
}

// Output:
{
  id: "uuid-1",
  name: "Java Backend 1",
  description: "...",
  schoolClassName: "TE24A",   // ← transformerat
  leadTeacher: {
    username: "lars",
    email: "lars@example.com"
  },
  assistants: [],
  assignments: [
    { id: "a1", title: "Inlämning 1", status: "OPEN", createdAt: "2026-01-01" }
  ]
}
```

---

## 5. Komponenter

### `components/dashboard/CourseListView.jsx`

Visar kurser, antingen som grid eller table.

**Props:**
```javascript
<CourseListView
  courses={coursesArray}      // Obligatorisk
  view="grid"                // "grid" | "table" (default: "grid")
  role="student"             // "student" | "teacher" (anpassar kolumner)
/>
```

**Dataflöde:**
```javascript
// I Dashboard.jsx
const { courses, loading, error } = useCourses();
const mappedCourses = mapToCardFormat(courses);

<CourseListView courses={mappedCourses} view="grid" role="student" />
```

**Grid vy:**
- Visar `CourseCard` för varje kurs
- 3 kolumner

**Table vy:**
- Student: Kurs, Klass, Status, Tidslinje, Moment
- Teacher: Kurs, Klass, Studenter, Uppgifter

---

### `components/dashboard/AssignmentListView.jsx`

Visar uppdrag i tabell.

**Props:**
```javascript
<AssignmentListView
  assignments={assignmentsArray}  // Obligatorisk
  title="Uppgifter"              // Rubrik (default: "Uppgifter")
  subtitle="5 uppgifter"         // Underrubrik (valfri)
  loading={false}                // Loading state (valfri)
  error={null}                   // Error message (valfri)
  emptyMessage="Inga uppgifter"  // Empty state (valfri)
/>
```

**Dataflöde:**
```javascript
// I Dashboard.jsx (student)
const { assignments, loading, error } = useAssignments();

<AssignmentListView
  assignments={assignments}
  loading={loading}
  error={error}
/>

// I CourseDetailPage.jsx (kursspecifika uppdrag)
<AssignmentListView
  assignments={courseData.assignments}
  title={`Uppgifter i ${courseData.name}`}
  subtitle={`${courseData.assignments.length} uppgift(er)`}
/>
```

**Visar:**
- Uppgift (title)
- Status (Badge: Öppen, Stängd, Skapad)
- Skapad (datum)
- Uppdaterad (datum)

---

## 6. Pages

### `pages/Dashboard.jsx`

**Student:**
```javascript
// Hämta data
const { courses, loading: coursesLoading } = useCourses();
const { assignments, loading: assignmentsLoading } = useAssignments();

// Transformera kursdata
const mappedCourses = mapToCardFormat(courses);

// Visa
<TabsContent value="overview">
  <CourseListView courses={mappedCourses} view="grid" role="student" />
</TabsContent>

<TabsContent value="courses">
  <CourseListView courses={mappedCourses} view="grid" role="student" />
</TabsContent>

<TabsContent value="assignments">
  <AssignmentListView
    assignments={assignments}
    loading={assignmentsLoading}
    error={assignmentsError}
  />
</TabsContent>
```

**Teacher:**
```javascript
<TabsContent value="overview">
  <TeacherOverview user={user} />
  // TODO: Använd CourseListView med role="teacher"
</TabsContent>

<TabsContent value="courses">
  <CourseListView courses={mappedCourses} view="table" role="teacher" />
</TabsContent>
```

---

### `pages/CourseDetailPage.jsx`

```javascript
// Hämta kursdata
const { course, loading, error } = useCourseDetail(courseId);

// Transformera data
const courseData = mapToCourseDetailFormat(course);

// Visa uppdrag
<TabsContent value="assignments">
  <AssignmentListView
    assignments={courseData.assignments}
    title={`Uppgifter i ${courseData.name}`}
    subtitle={`${courseData.assignments.length} uppgift(er)`}
  />
</TabsContent>
```

---

## 7. Komplett Exempel: Från API till UI

### Exempel 1: Visa alla kurser för en student

```javascript
// 1. Page: Dashboard.jsx
import { useCourses } from '@/hooks/useCourses';
import { mapToCardFormat } from '@/mappers/courseMapper';
import { CourseListView } from '@/components/dashboard/CourseListView';

export default function Dashboard() {
  // 2. Hämta data från API
  const { courses, loading, error } = useCourses();

  // 3. Transformera data
  const mappedCourses = mapToCardFormat(courses);

  // 4. Visa data
  return (
    <CourseListView
      courses={mappedCourses}
      view="grid"
      role="student"
    />
  );
}

// ---
// Dataflöde:

// API Response:
// GET /api/courses
// [
//   {
//     "id": "uuid-1",
//     "name": "Java Backend 1",
//     "schoolClass": { "name": "TE24A" },
//     "leadTeacher": { "username": "lars" }
//   }
// ]

// ↓ courseApi.getAllCourses()

// Hook return:
// { courses: [...], loading: false, error: null }

// ↓ mapToCardFormat(courses)

// Mapped data:
// [
//   {
//     "id": "uuid-1",
//     "name": "Java Backend 1",
//     "class": "TE24A",
//     "teacher": "lars"
//   }
// ]

// ↓ CourseListView

// UI: Grid med CourseCard komponenter
```

---

### Exempel 2: Visa alla uppdrag för en student

```javascript
// 1. Page: Dashboard.jsx
import { useAssignments } from '@/hooks/useAssignments';
import { AssignmentListView } from '@/components/dashboard/AssignmentListView';

export default function Dashboard() {
  // 2. Hämta data från API
  const { assignments, loading, error } = useAssignments();

  // 3. Ingen mapping (data är redan i rätt format)

  // 4. Visa data
  return (
    <AssignmentListView
      assignments={assignments}
      loading={loading}
      error={error}
    />
  );
}

// ---
// Dataflöde:

// API Response:
// GET /api/assignments
// [
//   {
//     "id": "uuid-1",
//     "title": "Inlämning: Java Streams",
//     "status": "OPEN",
//     "createdAt": "2026-04-01T10:00:00",
//     "updatedAt": "2026-04-01T10:00:00"
//   }
// ]

// ↓ assignmentApi.getAllAssignments()

// Hook return:
// { assignments: [...], loading: false, error: null }

// ↓ (ingen mapping)

// ↓ AssignmentListView

// UI: Tabell med kolumner
// - Uppgift: "Inlämning: Java Streams"
// - Status: Badge "Öppen"
// - Skapad: "2026-04-01"
// - Uppdaterad: "2026-04-01"
```

---

### Exempel 3: Visa kursspecifika uppdrag

```javascript
// 1. Page: CourseDetailPage.jsx
import { useParams } from 'react-router-dom';
import { useCourseDetail } from '@/hooks/useCourseDetail';
import { mapToCourseDetailFormat } from '@/mappers/courseMapper';
import { AssignmentListView } from '@/components/dashboard/AssignmentListView';

export default function CourseDetailPage() {
  const { courseId } = useParams();

  // 2. Hämta kursdata med uppdrag
  const { course, loading, error } = useCourseDetail(courseId);

  // 3. Transformera data
  const courseData = mapToCourseDetailFormat(course);

  // 4. Visa uppdrag
  return (
    <AssignmentListView
      assignments={courseData.assignments}
      title={`Uppgifter i ${courseData.name}`}
    />
  );
}

// ---
// Dataflöde:

// API Response:
// GET /api/courses/{id}
// {
//   "id": "uuid-1",
//   "name": "Java Backend 1",
//   "assignments": [
//     {
//       "id": "a1",
//       "title": "Inlämning 1",
//       "status": "OPEN"
//     }
//   ]
// }

// ↓ courseApi.getCourseById(id)

// Hook return:
// { course: {...}, loading: false, error: null }

// ↓ mapToCourseDetailFormat(course)

// Mapped data:
// {
//   "name": "Java Backend 1",
//   "schoolClassName": "TE24A",
//   "assignments": [
//     { "id": "a1", "title": "Inlämning 1", "status": "OPEN" }
//   ]
// }

// ↓ AssignmentListView

// UI: Tabell med kursens uppdrag
```

---

## 8. Lägga till ny funktionalitet

### För att hämta en ny typ av data:

1. **Skapa API client** i `api/`
```javascript
// api/assignments.js
export const assignmentApi = {
  getAll: async () => { ... }
};
```

2. **Skapa hook** i `hooks/`
```javascript
// hooks/useAssignments.js
export function useAssignments() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const result = await assignmentApi.getAll();
        setData(result);
      } finally {
        setLoading(false);
      }
    };
    void fetchData();
  }, []);

  return { data, loading, error };
}
```

3. **Skapa view-komponent** (valfritt)
```javascript
// components/AssignmentListView.jsx
export function AssignmentListView({ data, loading, error }) {
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {data.map(item => <div key={item.id}>{item.name}</div>)}
    </div>
  );
}
```

4. **Använd i page**
```javascript
import { useAssignments } from '@/hooks/useAssignments';
import { AssignmentListView } from '@/components/AssignmentListView';

export default function Page() {
  const { data, loading, error } = useAssignments();

  return <AssignmentListView data={data} loading={loading} error={error} />;
}
```

---

## 9. Viktiga principer

### ✅ Gör så här:
- Använd hooks för datahämtning
- Hantera alltid `loading`, `error`, och `empty` states
- Använd mappers för att transformera data
- Håll komponenter rena - separera datahämtning från presentation
- Använd `CourseListView` och `AssignmentListView` för konsekvent UI

### ❌ Undvik:
- Att anropa API direkt i komponenter (använd hooks)
- Att transformera data i komponenter (använd mappers)
- Att duplicera datahämtning på flera ställen (använd hooks)
- Att hårdkoda data (hämta från API)

---

## 10. Struktur

```
frontend/src/
├── api/                    # API clients
│   ├── client.js          # Axios konfiguration
│   ├── courses.js         # Kurs API
│   └── assignments.js     # Uppdrag API
│
├── hooks/                 # Datahämtning
│   ├── useCourses.js
│   ├── useCourseDetail.js
│   └── useAssignments.js
│
├── mappers/               # Data transformation
│   └── courseMapper.js
│
├── components/            # UI Komponenter
│   └── dashboard/
│       ├── CourseListView.jsx
│       ├── AssignmentListView.jsx
│       └── CourseCard.jsx
│
└── pages/                 # Sidor
    ├── Dashboard.jsx
    └── CourseDetailPage.jsx
```

---

## 11. Debugging

### Problemlösning:

**Data laddas inte:**
1. Kolla att API endpoint finns i backend
2. Kolla att `api/` filen anropar rätt endpoint
3. Kolla att `hook` anropar API-funktionen
4. Kolla console för errors

**Fel data visas:**
1. Kolla API response (network tab i browser)
2. Kolla att mappers transformerar korrekt
3. Kolla att komponenten får rätt props

**Loading spinner visas aldrig:**
1. Kontrollera att `loading` state används
2. Kontrollera att `finally` sätter `loading(false)`

---

## 12. Terminologi

| Term | Förklaring |
|------|-------------|
| **API Client** | Funktioner som anropar backend API |
| **Hook** | React-funktion som hanterar state och datahämtning |
| **Mapper** | Funktion som transformerar data från ett format till ett annat |
| **Component** | React-komponent som visar data i UI |
| **Page** | Sida (route) som använder komponenter och hooks |

---

För mer information, se källkoden i respektive fil.
