import * as React from "react"

import { cn } from "@/lib/utils"

function Card({
                className,
                size = "default",
                variant = "default",
                ...props
              }) {
  return (
    <div
      data-slot="card"
      data-size={size}
      data-variant={variant}
      className={cn(
        "group/card flex flex-col gap-2 overflow-hidden rounded-2xl py-4 has-[>img:first-child]:pt-0",
        "data-[size=sm]:gap-4 data-[size=sm]:py-4 *:[img:first-child]:rounded-t-xl *:[img:last-child]:rounded-b-xl",
        {
          default: "bg-card text-card-foreground ring-1 drop-shadow-md dark:drop-shadow-none drop-shadow-gray-300/50 ring-foreground/10",
          default_gray: "bg-gray-50 text-card-foreground ring-1 shadow-md shadow-gray-300/50 ring-foreground/10",
          flat_gray: "bg-gray-50 shadow-none ring-0 gap-2"
        }[variant],
        className
      )}
      {...props} />
  );
}

function CardHeader({
                      className,
                      ...props
                    }) {
  return (
    <div
      data-slot="card-header"
      className={cn(
        "group/card-header @container/card-header grid auto-rows-min items-start gap-2 rounded-t-xl px-6 group-data-[size=sm]/card:px-4 has-data-[slot=card-action]:grid-cols-[1fr_auto] has-data-[slot=card-description]:grid-rows-[auto_auto] [.border-b]:pb-6 group-data-[size=sm]/card:[.border-b]:pb-4",
        className
      )}
      {...props} />
  );
}

function CardTitle({
                     className,
                     ...props
                   }) {
  return (
    <div
      data-slot="card-title"
      className={cn("font-heading text-lg font-medium", className)}
      {...props} />
  );
}

function CardDescription({
                           className,
                           ...props
                         }) {
  return (
    <div
      data-slot="card-description"
      className={cn("text-sm text-muted-foreground", className)}
      {...props} />
  );
}

function CardAction({
                      className,
                      ...props
                    }) {
  return (
    <div
      data-slot="card-action"
      className={cn(
        "col-start-2 row-span-2 row-start-1 self-start justify-self-end",
        className
      )}
      {...props} />
  );
}

function CardContent({
                       className,
                       ...props
                     }) {
  return (
    <div
      data-slot="card-content"
      className={cn("px-6 group-data-[size=sm]/card:px-4", className)}
      {...props} />
  );
}

function CardFooter({
                      className,
                      ...props
                    }) {
  return (
    <div
      data-slot="card-footer"
      className={cn(
        "flex items-center rounded-b-xl px-6 group-data-[size=sm]/card:px-4 [.border-t]:pt-6 group-data-[size=sm]/card:[.border-t]:pt-4",
        className
      )}
      {...props} />
  );
}

export {
  Card,
  CardHeader,
  CardFooter,
  CardTitle,
  CardAction,
  CardDescription,
  CardContent,
}
