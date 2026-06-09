interface AboutModalProps {
  open: boolean;
  onClose: () => void;
}

const SHORTCUTS: { key: string; description: string }[] = [
  { key: 'D', description: 'Switch to design mode' },
  { key: 'L', description: 'Switch to launch mode' },
  { key: 'Space', description: 'Launch / pause / resume the flight' },
  { key: 'R', description: 'Reset the flight' },
  { key: '?', description: 'Toggle this about dialog' },
  { key: 'Esc', description: 'Close dialogs' },
];

export function AboutModal({ open, onClose }: AboutModalProps) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-ink/30 p-4">
      <div className="bg-white rounded-lg shadow-xl border border-nasa/20 w-full max-w-lg p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-nasa">About RocketModeler</h2>
          <button
            type="button"
            onClick={onClose}
            className="text-ink/40 hover:text-ink text-xl leading-none"
            aria-label="Close"
          >
            ×
          </button>
        </div>

        <p className="text-sm text-ink/80">
          Inspired by NASA Glenn Research Center's RocketModeler (Eric Bishop, c. 2002). The
          original applet is public-domain US government work. This is an independent modern
          rebuild, not affiliated with or endorsed by NASA.
        </p>

        <div className="text-sm text-ink/70 space-y-2">
          <p>
            <strong>Built with:</strong> TypeScript, React, Vite, Tailwind CSS, Web Audio API.
          </p>
          <p>
            <strong>Physics:</strong> CG ported from the original applet plus upgraded
            Barrowman center-of-pressure with shape-aware nose terms, ISA troposphere
            atmosphere, launch-rod constraint, and an aero-stability gravity turn.
          </p>
          <p>
            <strong>Source:</strong>{' '}
            <span className="font-mono text-xs">github.com/harshcasper/rocket-modeler</span>
          </p>
        </div>

        <div className="space-y-2">
          <h3 className="text-sm font-semibold text-nasa">Keyboard shortcuts</h3>
          <dl className="grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-xs text-ink/70">
            {SHORTCUTS.map((s) => (
              <div key={s.key} className="contents">
                <dt>
                  <kbd className="font-mono text-[10px] bg-paper border border-nasa/20 rounded px-1.5 py-0.5">
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
