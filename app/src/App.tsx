import { useAppStore } from './state/store';
import { DesignMode } from './design/DesignMode';
import { FlightMode } from './flight/FlightMode';

export default function App() {
  const mode = useAppStore((s) => s.mode);
  const setMode = useAppStore((s) => s.setMode);

  return (
    <div className="min-h-screen flex flex-col bg-paper text-ink">
      <header className="flex items-center gap-4 px-6 py-3 border-b border-nasa/15 bg-white">
        <h1 className="text-xl font-semibold tracking-tight text-nasa">
          RocketModeler
          <span className="ml-2 text-xs font-normal text-ink/50">v1</span>
        </h1>
        <nav className="ml-4 inline-flex rounded-full border border-nasa/20 p-1 bg-paper">
          <button
            type="button"
            onClick={() => setMode('design')}
            className={
              'px-4 py-1 rounded-full text-sm font-medium transition-colors ' +
              (mode === 'design'
                ? 'bg-nasa text-white shadow-sm'
                : 'text-nasa hover:bg-nasa/10')
            }
          >
            Design
          </button>
          <button
            type="button"
            onClick={() => setMode('flight')}
            className={
              'px-4 py-1 rounded-full text-sm font-medium transition-colors ' +
              (mode === 'flight'
                ? 'bg-nasa text-white shadow-sm'
                : 'text-nasa hover:bg-nasa/10')
            }
          >
            Launch ▸
          </button>
        </nav>
        <div className="ml-auto text-xs text-ink/50 hidden md:block">
          inspired by NASA Glenn's RocketModeler
        </div>
      </header>
      <main className="flex-1 min-h-0">
        {mode === 'design' ? <DesignMode /> : <FlightMode />}
      </main>
    </div>
  );
}
