// Two-way sync between the URL hash and the rocket store. Reading the hash
// on mount overrides localStorage; subsequent rocket edits debounce-write back
// to the hash so the address bar is always a shareable link.

import { useEffect, useRef } from 'react';
import { useAppStore } from '../state/store';
import { decodeRocket, encodeRocket } from './codec';

export function useUrlSync() {
  const setRocket = useAppStore((s) => s.setRocket);
  const writeTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const hydratedRef = useRef(false);

  // Read on mount.
  useEffect(() => {
    if (hydratedRef.current) return;
    hydratedRef.current = true;
    if (typeof window === 'undefined') return;
    const fromHash = decodeRocket(window.location.hash);
    if (fromHash) setRocket(fromHash);
  }, [setRocket]);

  // Write debounced.
  useEffect(() => {
    return useAppStore.subscribe(
      (s) => s.rocket,
      (rocket) => {
        if (typeof window === 'undefined') return;
        if (writeTimer.current) clearTimeout(writeTimer.current);
        writeTimer.current = setTimeout(() => {
          const newHash = encodeRocket(rocket);
          // Use history.replaceState so we don't pollute the back stack.
          const url = new URL(window.location.href);
          url.hash = newHash.slice(1); // strip leading '#'
          window.history.replaceState(null, '', url.toString());
        }, 250);
      },
    );
  }, []);
}

export function copyShareLink(): Promise<boolean> {
  if (typeof window === 'undefined' || !window.navigator?.clipboard) {
    return Promise.resolve(false);
  }
  return window.navigator.clipboard
    .writeText(window.location.href)
    .then(() => true)
    .catch(() => false);
}
