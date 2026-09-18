import { XIcon } from "lucide-react";
import type { ReactNode } from "react";

type DialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
};

type DialogContentProps = {
  children: ReactNode;
  className?: string;
};

export function Dialog({ open, onOpenChange, children }: DialogProps) {
  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onOpenChange(false);
      }}
    >
      {children}
    </div>
  );
}

export function DialogContent({
  children,
  className = "",
}: DialogContentProps) {
  return (
    <div
      role="dialog"
      aria-modal="true"
      className={`relative w-full max-w-lg rounded-lg border bg-background p-6 shadow-xl ${className}`}
    >
      {children}
    </div>
  );
}

export function DialogHeader({ children }: { children: ReactNode }) {
  return <div className="mb-5 space-y-1.5">{children}</div>;
}

export function DialogTitle({ children }: { children: ReactNode }) {
  return <h2 className="text-lg font-semibold">{children}</h2>;
}

export function DialogDescription({ children }: { children: ReactNode }) {
  return <p className="text-sm text-muted-foreground">{children}</p>;
}

export function DialogClose({ onClick }: { onClick: () => void }) {
  return (
    <button
      type="button"
      aria-label="Close"
      className="absolute right-4 top-4 rounded-sm opacity-70 hover:opacity-100"
      onClick={onClick}
    >
      <XIcon className="size-4" />
    </button>
  );
}
