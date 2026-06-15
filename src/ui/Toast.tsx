import { useEffect, useState } from 'react';

type ToastKind = 'info' | 'success' | 'error';

interface ToastItem {
  id: number;
  kind: ToastKind;
  text: string;
  ttlMs: number;
}

let listeners: Array<(t: ToastItem) => void> = [];
let nextId = 1;

export function pushToast(text: string, kind: ToastKind = 'info', ttlMs = 2400) {
  const t: ToastItem = { id: nextId++, text, kind, ttlMs };
  for (const fn of listeners) fn(t);
}

export function ToastHost() {
  const [items, setItems] = useState<ToastItem[]>([]);

  useEffect(() => {
    const onPush = (t: ToastItem) => {
      setItems((prev) => [...prev, t]);
      setTimeout(() => {
        setItems((prev) => prev.filter((x) => x.id !== t.id));
      }, t.ttlMs);
    };
    listeners.push(onPush);
    return () => {
      listeners = listeners.filter((l) => l !== onPush);
    };
  }, []);

  if (items.length === 0) return null;

  return (
    <div
      className="fixed top-3 right-3 z-50 flex flex-col gap-2 pointer-events-none"
      role="status"
      aria-live="polite"
    >
      {items.map((t) => (
        <div
          key={t.id}
          className={
            'pointer-events-auto rounded-md border px-3 py-2 text-xs font-medium shadow-md backdrop-blur-sm bg-white/95 animate-[fadeSlide_180ms_ease-out] ' +
            (t.kind === 'success'
              ? 'border-stability-good text-stability-good'
              : t.kind === 'error'
                ? 'border-stability-bad text-stability-bad'
                : 'border-nasa/30 text-nasa')
          }
        >
          {t.text}
        </div>
      ))}
    </div>
  );
}
