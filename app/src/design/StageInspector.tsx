import { useAppStore } from '../state/store';

export function StageInspector() {
  const rocket = useAppStore((s) => s.rocket);
  const stagesShowing = useAppStore((s) => s.stagesShowing);
  const setStagesShowing = useAppStore((s) => s.setStagesShowing);

  if (rocket.numStages === 1) return null;

  const options: { n: 1 | 2 | 3; label: string }[] = [];
  for (let i = rocket.numStages; i >= 1; i--) {
    const stages = i as 1 | 2 | 3;
    if (stages === rocket.numStages) options.push({ n: stages, label: 'Full rocket' });
    else if (stages === 1) options.push({ n: stages, label: 'Top stage only' });
    else options.push({ n: stages, label: `Top ${stages} stages` });
  }

  return (
    <div className="flex items-center gap-2 text-xs">
      <span className="text-ink/40 uppercase tracking-wider">Inspect</span>
      <div className="inline-flex rounded-full border border-nasa/20 p-0.5 bg-paper">
        {options.map((opt) => (
          <button
            key={opt.n}
            type="button"
            onClick={() => setStagesShowing(opt.n)}
            className={
              'px-3 py-0.5 rounded-full text-xs font-medium transition-colors ' +
              (stagesShowing === opt.n
                ? 'bg-nasa text-white'
                : 'text-nasa hover:bg-nasa/10')
            }
          >
            {opt.label}
          </button>
        ))}
      </div>
    </div>
  );
}
