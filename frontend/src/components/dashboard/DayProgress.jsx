import { CircleHelp } from 'lucide-react';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

export function DayProgress({ course }) {
  const now = new Date();

  const parseLocalDate = (dateStr) => {
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day);
  };

  const start = parseLocalDate(course.startDate);
  const end = parseLocalDate(course.endDate);

  const totalDays = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
  const daysPassed = Math.max(0, Math.min(totalDays, Math.ceil((now - start) / (1000 * 60 * 60 * 24)) + 1));

  // Helper to get state color for important dates
  const getStateColor = (importantDate) => {
    if (!importantDate?.userAssignmentStatus) return null;

    switch (importantDate.userAssignmentStatus) {
      case 'EVALUATED':
        return importantDate.grade ? 'green' : 'yellow';
      case 'TURNED_IN':
        return 'yellow';
      case 'FAILED':
        return 'red';
      default:
        return null;
    }
  };

  // Get important dates mapped to day index
  const importantDatesMap = {};
  if (course.importantDates && course.importantDates.length > 0) {
    course.importantDates.forEach(({ date, type, label, userAssignmentStatus, grade }) => {
      const eventDate = parseLocalDate(date);
      const dayIndex = Math.floor((eventDate - start) / (1000 * 60 * 60 * 24));
      if (dayIndex >= 0 && dayIndex < totalDays) {
        importantDatesMap[dayIndex] = { type, label, userAssignmentStatus, grade };
      }
    });
  }

  // Max 90 days displayed
  const maxSquares = 90;
  const squaresToDisplay = Math.min(totalDays, maxSquares);

  // Helper to format date as YYYY-MM-DD
  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // Build content for tooltip
  const getLegendContent = () => {
    const hasImportantDates = course.importantDates && course.importantDates.length > 0;
    if (!hasImportantDates) return null;

    return (
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-1">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 bg-orange-500" />
            <span>Tentamen</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 bg-purple-500" />
            <span>Inlämning</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 bg-yellow-500" />
            <span>Inlämnad</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 bg-green-500" />
            <span>Godkänd</span>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-px">
        {Array.from({ length: squaresToDisplay }).map((_, i) => {
          const isPast = i < daysPassed;
          const isCurrentDay = i === daysPassed - 1 && daysPassed > 0;
          const importantDate = importantDatesMap[i];

          return (
            <div
              key={i}
              className={`
                h-4 flex-1 transition-all relative rounded-xs
                ${importantDate
                  ? importantDate.type === 'exam'
                    ? 'bg-orange-500'
                    : getStateColor(importantDate) === 'green'
                      ? 'bg-green-500'
                      : getStateColor(importantDate) === 'yellow'
                        ? 'bg-yellow-500'
                        : 'bg-purple-500'
                  : isPast
                    ? 'bg-primary'
                    : isCurrentDay
                      ? 'bg-primary scale-200'
                      : 'bg-muted'
                }
              `}
              title={importantDate ? `${importantDate.label} (${importantDate.userAssignmentStatus || 'Ej påbörjad'})` : formatDate(new Date(start.getTime() + i * 24 * 60 * 60 * 1000))}
            />
          );
        })}
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <CircleHelp className="h-5 w-5 ml-2 shrink-0 transition-colors text-muted-foreground"/>
            </TooltipTrigger>
            <TooltipContent>
              {getLegendContent()}
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>
    </div>
  );
}
