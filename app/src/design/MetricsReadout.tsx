import { useMemo } from 'react';
import { useAppStore } from '../state/store';
import { computeStageCg } from '../physics/cg';
import { computeCpForRocket } from '../physics/cp-barrowman';
import { cm, grams } from '../ui/format';
import { StabilityGauge } from './StabilityGauge';
import { StageInspector } from './StageInspector';

export function MetricsReadout() {
  const rocket = useAppStore((s) => s.rocket);
  const stagesShowing = useAppStore((s) => s.stagesShowing);

  const { cg, totalMassG, cp } = useMemo(() => {
    const cgRes = computeStageCg(rocket, stagesShowing);
    const cpRes = computeCpForRocket(rocket);
    return { cg: cgRes.cg, totalMassG: cgRes.totalMassG, cp: cpRes.cp };
  }, [rocket, stagesShowing]);

  return (
    <div className="space-y-3">
      <StageInspector />
      <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm">
        <Metric label="CG" value={cm(cg)} />
        <Metric label="CP" value={cm(cp)} />
        <Metric label="Mass" value={grams(totalMassG)} />
      </div>
      <StabilityGauge />
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
