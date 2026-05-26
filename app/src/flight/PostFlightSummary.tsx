import type { FlightSample } from '../domain/types';
import { FlightChart } from './FlightChart';

interface PostFlightSummaryProps {
  samples: FlightSample[];
  onClose: () => void;
  onShare?: () => void;
}

export function PostFlightSummary({ samples, onClose, onShare }: PostFlightSummaryProps) {
  if (samples.length === 0) return null;

  const last = samples[samples.length - 1];
  const maxAlt = samples.reduce((m, s) => Math.max(m, s.altitude), 0);
  const peakSpeed = samples.reduce((m, s) => Math.max(m, s.speed), 0);
  const tToApogee = (() => {
    let bestT = 0;
    let bestAlt = 0;
    for (const s of samples) {
      if (s.altitude > bestAlt) {
        bestAlt = s.altitude;
        bestT = s.t;
      }
    }
    return bestT;
  })();
  const totalTime = last.t;

  const crashed = last.phase === 'crashed';

  return (
    <div className="absolute inset-0 z-10 grid place-items-center bg-ink/30 p-4">
      <div className="bg-white rounded-lg shadow-xl border border-nasa/20 w-full max-w-xl p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-nasa">
            {crashed ? 'Flight failed' : 'Flight complete'}
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="text-ink/40 hover:text-ink text-xl leading-none"
            aria-label="Close"
          >
            ×
          </button>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
          <Stat label="Max altitude" value={`${maxAlt.toFixed(1)} m`} highlight />
          <Stat label="Peak speed" value={`${peakSpeed.toFixed(1)} m/s`} />
          <Stat label="Time to apogee" value={`${tToApogee.toFixed(1)} s`} />
          <Stat label="Total flight" value={`${totalTime.toFixed(1)} s`} />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <FlightChart samples={samples} field="altitude" label="Altitude" unit="m" color="#0B3D91" />
          <FlightChart samples={samples} field="speed" label="Speed" unit="m/s" color="#D63333" />
        </div>

        <div className="flex justify-end gap-2 pt-2">
          {onShare && (
            <button
              type="button"
              onClick={onShare}
              className="px-4 py-1.5 rounded text-sm font-medium border border-nasa/30 text-nasa hover:bg-nasa/10"
            >
              Copy share link
            </button>
          )}
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-1.5 rounded text-sm font-medium bg-nasa text-white hover:bg-nasa-light"
          >
            Done
          </button>
        </div>
      </div>
    </div>
  );
}

function Stat({
  label,
  value,
  highlight,
}: {
  label: string;
  value: string;
  highlight?: boolean;
}) {
  return (
    <div
      className={
        'rounded border px-3 py-2 ' +
        (highlight ? 'bg-nasa text-white border-nasa' : 'bg-paper border-nasa/15 text-ink')
      }
    >
      <div
        className={
          'text-[10px] uppercase tracking-wider ' + (highlight ? 'text-white/70' : 'text-ink/40')
        }
      >
        {label}
      </div>
      <div className="font-mono tabular-nums text-base">{value}</div>
    </div>
  );
}
