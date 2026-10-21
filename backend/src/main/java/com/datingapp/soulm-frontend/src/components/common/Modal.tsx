import { useEffect, useId, useRef, type ReactNode } from 'react';

interface ModalProps {
  open: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
  /** Ignore Escape while something is being submitted. */
  locked?: boolean;
}

/** Native <dialog>: focus is trapped, Escape closes it and the page behind is inert. Content mounts only while open. */
export function Modal({ open, title, onClose, children, locked = false }: ModalProps) {
  const ref = useRef<HTMLDialogElement>(null);
  const titleId = useId();

  useEffect(() => {
    const dialog = ref.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    else if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={ref}
      className="modal"
      aria-labelledby={titleId}
      onCancel={(event) => {
        event.preventDefault();
        if (!locked) onClose();
      }}
    >
      {open && (
        <div className="modal__content">
          <h2 id={titleId} className="modal__title">
            {title}
          </h2>
          {children}
        </div>
      )}
    </dialog>
  );
}
