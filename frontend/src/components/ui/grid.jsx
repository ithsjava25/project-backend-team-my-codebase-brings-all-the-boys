import * as React from "react"
import { cn } from "@/lib/utils"

const Grid = React.forwardRef(({ className, cols = 3, gap = "4", children, ...props }, ref) => {
  const gridCols = {
    1: "grid-cols-1",
    2: "grid-cols-1 md:grid-cols-2",
    3: "grid-cols-1 md:grid-cols-2 lg:grid-cols-3",
    4: "grid-cols-1 md:grid-cols-2 lg:grid-cols-4",
    auto: "grid-cols-[repeat(auto-fill,minmax(250px,1fr))]"
  }[cols] || cols

  const gapClass = gap === "0" ? "gap-0" : `gap-${gap}`

  return (
    <div
      ref={ref}
      className={cn("grid", gridCols, gapClass, className)}
      {...props}
    >
      {children}
    </div>
  )
})
Grid.displayName = "Grid"

export { Grid }
