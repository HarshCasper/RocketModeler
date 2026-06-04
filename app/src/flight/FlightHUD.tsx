import type { FlightSample } from '../domain/types';
import { num } from '../ui/format';
import { GRAVITY } from '../domain/constants';

interface FlightHUDProps {
  sample: FlightSample | null;
  maxAlt: number;
}

export function FlightHUD({ sample, maxAlt }: FlightHUDProps) {
  const accelG = sample ? sample.acceleration / GRAVITY : 0;
  const phaseLabel = sample
    ? sample.onRod && (sample.phase === 'boost')
      ? 'ON ROD'
      : sample.phase.toUpperCase()
    : 'IDLE';
  const marginTone = !sample
    ? 'neutral'
    : sample.marginCal < 0
      ? 'bad'
      : sample.marginCal < 1
        ? 'warn'
        : sample.marginCal > 2.5
          ? 'warn'
          : 'good';
  return (
    <div className="grid grid-cols-2 gap-3">
      <Cell label="Time" value={`${num(sample?.t ?? 0, 2)} s`} />
      <Cell label="Phase" value={phaseLabel} accent />
      <Cell label="Altitude" value={`${num(sample?.altitude ?? 0, 1)} m`} />
      <Cell label="Speed" value={`${num(sample?.speed ?? 0, 1)} m/s`} />
      <Cell label="Max alt" value={`${num(maxAlt, 1)} m`} accent />
      <Cell label="Accel" value={`${num(accelG, 1)} g`} />
      <Cell label="Stage" value={sample ? `${sample.activeStage + 1}` : '—'} />
      <Cell
        label="Margin"
        value={sample ? `${num(sample.marginCal, 2)} cal` : '—'}
        tone={marginTone}
      />
    </div>
  );
}

type CellTone = 'neutral' | 'good' | 'warn' | 'bad';

function Cell({
  label,
  value,
  accent,
  tone = 'neutral',
}: {
  label: string;
  value: string;
  accent?: boolean;
  tone?: CellTone;
}) {
  const toneClass = accent
    ? 'bg-nasa text-white border-nasa'
    : tone === 'good'
      ? 'bg-stability-good/10 text-stability-good border-stability-good/30'
      : tone === 'warn'
        ? 'bg-stability-warn/10 text-stability-warn border-stability-warn/30'
        : tone === 'bad'
          ? 'bg-stability-bad/10 text-stability-bad border-stability-bad/30'
          : 'bg-paper border-nasa/15 text-ink';
  return (
    <div className={'rounded border px-2 py-1.5 ' + toneClass}>
      <div
        className={
          'text-[10px] uppercase tracking-wider ' +
          (accent ? 'text-white/70' : 'opacity-70')
        }
      >
        {label}
      </div>
      <div className="font-mono tabular-nums text-base">{value}</div>
    </div>
  );
}
