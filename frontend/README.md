# Frontend Documentation

---

## 📚 Dokumentation

### 🚀 [DATA_FLOW.md](./DATA_FLOW.md) - **Börja här!**
Komplett guide om hur data flödar från API till visning:
- API endpoints
- API clients
- Hooks
- Mappers
- Components
- Fullständiga exempel

---

## 🏗️ Projektstruktur

```
frontend/src/
├── api/              # API calls till backend
├── components/       # Återanvändbara UI-komponenter
│   ├── dashboard/   # Dashboard-specifika komponenter
│   └── ui/          # Shadcn/ui komponenter
├── hooks/           # Custom React hooks
├── mappers/         # Data transformation
├── pages/           # Sidor (routes)
└── context/         # React Context (Auth, Theme)
```

---

## 🎯 Snabbstart

### 1. Installera dependencies
```bash
cd frontend
npm install
```

### 2. Starta dev server
```bash
npm run dev
```

Applikationen öppnas på `http://localhost:5173`

### 3. Bygg för production
```bash
npm run build
```

---

## 🔑 Viktiga komponenter

### Dashboard Views
- **`CourseListView`** - Visar kurser (grid eller table)
- **`AssignmentListView`** - Visar uppdrag

### Pages
- **`Dashboard`** - Huvuddashboard (role-baserad)
- **`CourseDetailPage`** - Kursdetaljer

### Hooks
- **`useCourses`** - Hämta alla kurser
- **`useCourseDetail`** - Hämta en kurs med detaljer
- **`useAssignments`** - Hämta uppdrag
- **`useAuth`** - Authentication

---

## 📖 Hur lägger jag till ny funktionalitet?

Se [DATA_FLOW.md](./DATA_FLOW.md) avsnitt **"8. Lägga till ny funktionalitet"** för en steg-för-steg guide.

---

## 🛠️ Tillgängliga scripts

| Script | Beskrivning |
|--------|-------------|
| `npm run dev` | Starta dev server |
| `npm run build` | Bygg för production |
| `npm run lint` | Kör ESLint |
| `npm run preview` | Förhandsgranska production build |

---

## 📌 Viktigt om dataflöde

Alla data hämtas enligt detta mönster:

```
Backend API
    ↓
API Client (api/)
    ↓
Hook (hooks/)
    ↓
Mapper (mappers/) [valfritt]
    ↓
Component (components/)
    ↓
Page (pages/)
```

Läs mer i [DATA_FLOW.md](./DATA_FLOW.md)!

---

## 💡 Tips

- **Lägg till ny endpoint:** Skapa API client → Hook → Component → Page
- **Transformera data:** Använd mappers i `mappers/`
- **Visa data:** Använd t.ex. `CourseListView` eller `AssignmentListView`
- **Role-baserad UI:** Använd `role` prop på view-komponenter

---

## 📝 Tech Stack

- **React 18** - UI framework
- **Vite** - Build tool
- **React Router** - Routing
- **Axios** - HTTP client
- **Shadcn/ui** - UI components
- **Tailwind CSS** - Styling
- **Lucide Icons** - Icons

---

För frågor eller problem, se [DATA_FLOW.md](./DATA_FLOW.md) för detaljerad information.
