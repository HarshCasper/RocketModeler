import { useMemo } from 'react';
import { useAppStore } from '../state/store';
import { computeStageCg } from '../physics/cg';
import { computeCpForRocket } from '../physics/cp-barrowman';

type Verdict = 'unstable' | 'marginal' | 'stable' | 'overstable';

// Hobby-rocket rule of thumb: 1.0–2.5 caliber is the comfortable band. Below
// 1.0 is twitchy and may tumble in gusts; above 2.5 the rocket weathercocks
// into wind on the way up.
function verdictFor(caliber: number): Verdict {
  if (caliber < 0) return 'unstable';
  if (caliber < 1.0) return 'marginal';
  if (caliber > 2.5) return 'overstable';
  return 'stable';
}

const COLOR: Record<Verdict, string> = {
  unstable: '#C0392B',
  marginal: '#E0A116',
  stable: '#2E8B57',
  overstable: '#E0A116',
};

const LABEL: Record<Verdict, string> = {
  unstable: 'UNSTABLE',
  marginal: 'MARGINAL',
  stable: 'STABLE',
  overstable: 'OVERSTABLE',
};

export function StabilityGauge() {
  const rocket = useAppStore((s) => s.rocket);
  const stagesShowing = useAppStore((s) => s.stagesShowing);

  const { cg, cp, caliber } = useMemo(() => {
    const cgRes = computeStageCg(rocket, stagesShowing);
    const cpRes = computeCpForRocket(rocket);
    const cal = (cgRes.cg - cpRes.cp) / rocket.body.diameter;
    return { cg: cgRes.cg, cp: cpRes.cp, caliber: cal };
  }, [rocket, stagesShowing]);

  const verdict = verdictFor(caliber);
  const clamped = Math.max(-1, Math.min(3, caliber));
  const fillPercent = ((clamped + 1) / 4) * 100;

  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between text-xs">
        <span
          className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full font-mono tracking-wider"
          style={{
            backgroundColor: COLOR[verdict] + '20',
            color: COLOR[verdict],
            border: `1px solid ${COLOR[verdict]}50`,
          }}
        >
          <span
            className="inline-block w-1.5 h-1.5 rounded-full"
            style={{ backgroundColor: COLOR[verdict] }}
          />
          {LABEL[verdict]}
        </span>
        <span className="font-mono tabular-nums text-ink">
          {caliber.toFixed(2)} cal
        </span>
      </div>
      <div className="relative h-2 rounded-full bg-paper overflow-hidden border border-nasa/10">
        {/* zone backdrop */}
        <div
          className="absolute inset-0 flex"
          style={{
            background:
              'linear-gradient(90deg, #C0392B 0%, #C0392B 25%, #E0A116 25%, #E0A116 50%, #2E8B57 50%, #2E8B57 87.5%, #E0A116 87.5%, #E0A116 100%)',
            opacity: 0.25,
          }}
        />
        {/* needle */}
        <div
          className="absolute top-0 h-full w-[3px] bg-ink rounded-full transition-all"
          style={{ left: `calc(${fillPercent}% - 1.5px)` }}
        />
      </div>
      <div className="flex justify-between text-[10px] text-ink/40 font-mono tabular-nums">
        <span>−1</span>
        <span>0</span>
        <span>1</span>
        <span>2</span>
        <span>3</span>
      </div>
      <div className="flex justify-between text-xs">
        <span className="text-ink/60">
          CG <span className="font-mono tabular-nums text-ink">{cg.toFixed(1)}</span> cm
        </span>
        <span className="text-ink/60">
          CP <span className="font-mono tabular-nums text-ink">{cp.toFixed(1)}</span> cm
        </span>
      </div>
    </div>
  );
}
