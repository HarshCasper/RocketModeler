import { useState, useEffect } from 'react';
import { useAppStore } from '../state/store';
import { FlightViewer } from './FlightViewer';
import { FlightHUD } from './FlightHUD';
import { FlightControls } from './FlightControls';
import { PostFlightSummary } from './PostFlightSummary';
import { useSimulation } from './useSimulation';
import { isTypingTarget } from '../ui/keyboard';

export function FlightMode() {
  const rocket = useAppStore((s) => s.rocket);
  const flight = useAppStore((s) => s.flight);
  const sim = useSimulation({ rocket, config: flight });

  const [summaryOpen, setSummaryOpen] = useState(false);

  useEffect(() => {
    if (sim.runState === 'ended' && sim.samples.length > 0) {
      setSummaryOpen(true);
    }
  }, [sim.runState, sim.samples.length]);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.metaKey || e.ctrlKey || e.altKey) return;
      if (isTypingTarget(e.target)) return;
      if (e.key === ' ') {
        e.preventDefault();
        if (sim.runState === 'idle' || sim.runState === 'ended') {
          setSummaryOpen(false);
          sim.start();
        } else if (sim.runState === 'running') {
          sim.pause();
        } else if (sim.runState === 'paused') {
          sim.resume();
        }
      } else if (e.key === 'r' || e.key === 'R') {
        e.preventDefault();
        setSummaryOpen(false);
        sim.reset();
      }
    }
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [sim]);

  const dark = useAppStore((s) => s.dark);
  const showForces = useAppStore((s) => s.showForces);

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-4 p-4 h-full">
      <section className="relative rounded-lg border border-nasa/15 bg-white shadow-sm overflow-hidden dark:bg-ink dark:border-white/10">
        <FlightViewer
          rocket={rocket}
          sample={sim.sample}
          launchAngle={flight.launchAngle}
          countdown={sim.countdown}
          windSpeed={flight.windSpeed}
          showForces={showForces}
          dark={dark}
        />
        {summaryOpen && (
          <PostFlightSummary
            samples={sim.samples}
            onClose={() => setSummaryOpen(false)}
          />
        )}
      </section>
      <aside className="rounded-lg border border-nasa/15 bg-white shadow-sm p-4 space-y-4 overflow-y-auto dark:bg-ink/80 dark:border-white/10 dark:text-paper">
        <FlightHUD sample={sim.sample} maxAlt={sim.maxAlt} />
        <FlightControls
          runState={sim.runState}
          onStart={() => {
            setSummaryOpen(false);
            sim.start();
          }}
          onPause={sim.pause}
          onResume={sim.resume}
          onReset={() => {
            setSummaryOpen(false);
            sim.reset();
          }}
        />
      </aside>
    </div>
  );
}
