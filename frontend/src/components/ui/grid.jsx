import * as React from "react"
import { cn } from "@/lib/utils"

const Grid = React.forwardRef(({ className, cols = 3, gap = "4", children, ...props }, ref) => {
  const gridCols = {
    1: "grid-cols-1",
    2: "md:grid-cols-2 lg:grid-cols-4",
    3: "grid-cols-1 md:grid-cols-2 lg:grid-cols-3",
    4: "grid-cols-1 md:grid-cols-2 lg:grid-cols-4",
    auto: "grid grid-cols-[repeat(auto-fill,minmax(250px,1fr))]"
  }[cols] || cols

  return (
    <div
      ref={ref}
      className={cn("grid gap-4", gridCols, className)}
      {...props}
    >
      {children}
    </div>
  )
})
Grid.displayName = "Grid"

export { Grid }
