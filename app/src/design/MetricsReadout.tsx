import { useMemo } from 'react';
import { useAppStore } from '../state/store';
import { computeStageCg } from '../physics/cg';
import { cm, grams } from '../ui/format';

export function MetricsReadout() {
  const rocket = useAppStore((s) => s.rocket);
  const stagesShowing = useAppStore((s) => s.stagesShowing);

  const { cg, totalMassG } = useMemo(
    () => computeStageCg(rocket, stagesShowing),
    [rocket, stagesShowing],
  );

  return (
    <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm">
      <Metric label="CG" value={cm(cg)} />
      <Metric label="Mass" value={grams(totalMassG)} />
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <div className="text-[10px] uppercase tracking-wider text-ink/40">{label}</div>
      <div className="font-mono tabular-nums text-base text-ink">{value}</div>
    </div>
  );
}
