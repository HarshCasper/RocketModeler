import { useMemo } from 'react';
import type { FlightSample } from '../domain/types';

interface FlightChartProps {
  samples: FlightSample[];
  field: 'altitude' | 'speed';
  label: string;
  unit: string;
  color: string;
}

const W = 260;
const H = 80;
const PAD = 16;

export function FlightChart({ samples, field, label, unit, color }: FlightChartProps) {
  const { path, minVal, maxVal, tMax } = useMemo(() => {
    if (samples.length === 0) {
      return { path: '', minVal: 0, maxVal: 0, tMax: 0 };
    }
    const ts = samples.map((s) => s.t);
    const vs = samples.map((s) => s[field]);
    const tMaxLocal = Math.max(...ts);
    const lo = Math.min(...vs, 0);
    const hi = Math.max(...vs, 1);
    const span = hi - lo || 1;
    const scaleX = (W - 2 * PAD) / (tMaxLocal || 1);
    const scaleY = (H - 2 * PAD) / span;
    const pts = samples.map((s) => {
      const x = PAD + s.t * scaleX;
      const y = H - PAD - (s[field] - lo) * scaleY;
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    });
    return {
      path: 'M ' + pts.join(' L '),
      minVal: lo,
      maxVal: hi,
      tMax: tMaxLocal,
    };
  }, [samples, field]);

  if (samples.length === 0) return null;

  return (
    <div className="space-y-1">
      <div className="flex justify-between items-baseline text-[10px] text-ink/50">
        <span className="uppercase tracking-wider">{label}</span>
        <span className="font-mono tabular-nums">
          {maxVal.toFixed(1)} {unit}
        </span>
      </div>
      <svg viewBox={`0 0 ${W} ${H}`} className="w-full h-20">
        <rect x={0} y={0} width={W} height={H} fill="#F4F7FB" />
        <path d={path} stroke={color} strokeWidth={1.5} fill="none" />
        <text x={PAD} y={H - 2} fontSize={9} fill="#0B132080">
          0 s
        </text>
        <text x={W - PAD - 24} y={H - 2} fontSize={9} fill="#0B132080">
          {tMax.toFixed(1)} s
        </text>
        <text x={PAD} y={PAD - 4} fontSize={9} fill="#0B132080">
          {minVal.toFixed(1)} {unit}
        </text>
      </svg>
    </div>
  );
}
