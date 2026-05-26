import { useState, useEffect } from 'react';
import { useAppStore } from '../state/store';
import { FlightViewer } from './FlightViewer';
import { FlightHUD } from './FlightHUD';
import { FlightControls } from './FlightControls';
import { PostFlightSummary } from './PostFlightSummary';
import { useSimulation } from './useSimulation';

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

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-4 p-4 h-full">
      <section className="relative rounded-lg border border-nasa/15 bg-white shadow-sm overflow-hidden">
        <FlightViewer
          rocket={rocket}
          sample={sim.sample}
          launchAngle={flight.launchAngle}
          countdown={sim.countdown}
        />
        {summaryOpen && (
          <PostFlightSummary
            samples={sim.samples}
            onClose={() => setSummaryOpen(false)}
          />
        )}
      </section>
      <aside className="rounded-lg border border-nasa/15 bg-white shadow-sm p-4 space-y-4 overflow-y-auto">
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
