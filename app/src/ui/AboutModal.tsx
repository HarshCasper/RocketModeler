interface AboutModalProps {
  open: boolean;
  onClose: () => void;
}

const SHORTCUTS: { key: string; description: string }[] = [
  { key: 'D', description: 'Open the design view' },
  { key: 'L', description: 'Open the launch view' },
  { key: 'Space', description: 'Launch, pause, or resume the current flight' },
  { key: 'R', description: 'Reset the flight back to idle' },
  { key: '?', description: 'Open or close this dialog' },
  { key: 'Esc', description: 'Close dialogs' },
];

export function AboutModal({ open, onClose }: AboutModalProps) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-ink/40 dark:bg-black/60 p-4">
      <div className="bg-white dark:bg-ink rounded-lg shadow-xl border border-nasa/20 dark:border-white/10 w-full max-w-lg p-6 space-y-4 max-h-[90vh] overflow-y-auto text-ink dark:text-paper">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-nasa dark:text-rocket-tube">
            About RocketModeler
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="text-ink/40 dark:text-paper/50 hover:text-ink dark:hover:text-paper text-xl leading-none"
            aria-label="Close"
          >
            ×
          </button>
        </div>

        <p className="text-sm text-ink/80 dark:text-paper/75">
          RocketModeler is a browser rebuild of the NASA Glenn Research Center
          rocketry applet that Eric Bishop wrote in Java around 2002. The
          original ran in the browser plugin, which has been gone for years,
          so the design has been re-implemented as a static web app. The
          original applet is public-domain US Government work. This project
          is not affiliated with NASA.
        </p>

        <div className="text-sm text-ink/75 dark:text-paper/70 space-y-2">
          <p>
            <strong>Stack:</strong> Vite, React, TypeScript, Tailwind, Zustand,
            pako for hash compression, Framer Motion for the soft transitions,
            and the Web Audio API for the countdown beeps.
          </p>
          <p>
            <strong>Physics:</strong> centre of gravity is the original
            applet&apos;s mass-weighted calculation. Centre of pressure is
            Barrowman with shape-aware nose terms. Atmosphere is the ISA
            troposphere model. Flight integration is semi-Euler at 100
            sub-steps per visible frame, with a launch-rod axis constraint
            and an aero alignment term that produces gravity turn and
            weathercocking from wind and stability margin.
          </p>
          <p>
            <strong>Source:</strong>{' '}
            <span className="font-mono text-xs">
              github.com/harshcasper/rocket-modeler
            </span>
          </p>
        </div>

        <div className="space-y-2">
          <h3 className="text-sm font-semibold text-nasa dark:text-rocket-tube">
            Keyboard shortcuts
          </h3>
          <dl className="grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-xs text-ink/70 dark:text-paper/70">
            {SHORTCUTS.map((s) => (
              <div key={s.key} className="contents">
                <dt>
                  <kbd className="font-mono text-[10px] bg-paper dark:bg-ink/40 border border-nasa/20 dark:border-white/15 rounded px-1.5 py-0.5">
                    {s.key}
                  </kbd>
                </dt>
                <dd>{s.description}</dd>
              </div>
            ))}
          </dl>
        </div>

        <div className="flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-1.5 rounded text-sm font-medium bg-nasa text-white hover:bg-nasa-light"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
