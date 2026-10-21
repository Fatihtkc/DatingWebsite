import { useEffect, useState } from 'react';

/** Seconds remaining until `targetMs` (epoch ms); 0 when null or reached. Ticks once per second. */
export function useCountdown(targetMs: number | null): number {
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    if (targetMs === null) return;
    setNow(Date.now());
    const id = window.setInterval(() => {
      const current = Date.now();
      setNow(current);
      if (current >= targetMs) window.clearInterval(id);
    }, 1000);
    return () => window.clearInterval(id);
  }, [targetMs]);

  return targetMs === null ? 0 : Math.max(0, Math.ceil((targetMs - now) / 1000));
}
