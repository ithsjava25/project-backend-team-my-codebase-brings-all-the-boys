import * as React from "react"
import * as Dialog from "@radix-ui/react-dialog"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { XIcon } from "lucide-react"

function DialogRoot({
  ...props
}) {
  return <Dialog.Root data-slot="dialog" {...props} />;
}

function DialogTrigger({
  ...props
}) {
  return <Dialog.Trigger data-slot="dialog-trigger" {...props} />;
}

function DialogPortal({
  ...props
}) {
  return <Dialog.Portal data-slot="dialog-portal" {...props} />;
}

function DialogClose({
  ...props
}) {
  return <Dialog.Close data-slot="dialog-close" {...props} />;
}

function DialogOverlay({
  className,
  ...props
}) {
  return (
    <Dialog.Overlay
      data-slot="dialog-overlay"
      className={cn(
        "fixed inset-0 isolate z-50 bg-black/80 duration-100 supports-backdrop-filter:backdrop-blur-xs data-[state=open]:animate-in data-[state=open]:fade-in-0 data-[state=closed]:animate-out data-[state=closed]:fade-out-0",
        className
      )}
      {...props} />
  );
}

function DialogContent({
  className,
  children,
  showCloseButton = true,
  ...props
}) {
  return (
    <DialogPortal>
      <DialogOverlay />
      <Dialog.Content
        data-slot="dialog-content"
        className={cn(
          "fixed top-1/2 left-1/2 z-50 grid w-full max-w-[calc(100%-2rem)] -translate-x-1/2 -translate-y-1/2 gap-6 rounded-4xl bg-popover p-6 text-sm text-popover-foreground ring-1 ring-foreground/5 duration-100 outline-none sm:max-w-md data-[state=open]:animate-in data-[state=open]:fade-in-0 data-[state=open]:zoom-in-95 data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=closed]:zoom-out-95",
          className
        )}
        {...props}>
        {children}
        {showCloseButton && (
          <Dialog.Close data-slot="dialog-close" asChild>
            <Button variant="ghost" className="absolute top-4 right-4" size="icon-sm">
              <XIcon />
              <span className="sr-only">Close</span>
            </Button>
          </Dialog.Close>
        )}
      </Dialog.Content>
    </DialogPortal>
  );
}

function DialogHeader({
  className,
  ...props
}) {
  return (
    <div
      data-slot="dialog-header"
      className={cn("flex flex-col gap-2", className)}
      {...props} />
  );
}

function DialogFooter({
  className,
  showCloseButton = false,
  children,
  ...props
}) {
  return (
    <div
      data-slot="dialog-footer"
      className={cn("flex flex-col-reverse gap-2 sm:flex-row sm:justify-end", className)}
      {...props}>
      {children}
      {showCloseButton && (
        <Dialog.Close asChild>
          <Button variant="outline">Close</Button>
        </Dialog.Close>
      )}
    </div>
  );
}

function DialogTitle({
  className,
  ...props
}) {
  return (
    <Dialog.Title
      data-slot="dialog-title"
      className={cn("font-heading text-base leading-none font-medium", className)}
      {...props} />
  );
}

function DialogDescription({
  className,
  ...props
}) {
  return (
    <Dialog.Description
      data-slot="dialog-description"
      className={cn(
        "text-sm text-muted-foreground [&_a]:underline [&_a]::underline-offset-3 [&_a]::hover:text-foreground",
        className
      )}
      {...props} />
  );
}

export {
  DialogRoot as Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
}
