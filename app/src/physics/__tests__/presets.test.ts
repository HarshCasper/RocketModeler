import { describe, expect, it } from 'vitest';
import { PRESETS, PRESET_BY_ID } from '../../domain/presets';
import { computeStageCg } from '../cg';
import { computeCpForRocket } from '../cp-barrowman';
import { runFullFlight, apogee, peakSpeed } from '../simulate';
import { DEFAULT_FLIGHT_CONFIG } from '../../domain/defaults';

describe('preset rockets', () => {
  it('every preset has a non-negative caliber margin in its default loadout', () => {
    for (const preset of PRESETS) {
      const cg = computeStageCg(preset.rocket, preset.rocket.numStages).cg;
      const cp = computeCpForRocket(preset.rocket).cp;
      const cal = (cg - cp) / preset.rocket.body.diameter;
      expect(cal, `${preset.name} caliber margin`).toBeGreaterThan(0);
    }
  });

  it('Alpha III with A8-3 reaches a hobby-published apogee range', () => {
    const r = PRESET_BY_ID['alpha-iii'].rocket;
    const samples = runFullFlight(r, DEFAULT_FLIGHT_CONFIG);
    const a = apogee(samples);
    const v = peakSpeed(samples);
    // Estes lists Alpha III on A8-3 at roughly 40 m. Our simplified model
    // lacks launch-lug and launch-rod parasitic drag so it tends to overshoot;
    // accept anything in 15–160 m as a sane order-of-magnitude.
    expect(a).toBeGreaterThan(15);
    expect(a).toBeLessThan(160);
    expect(v).toBeGreaterThan(10);
    expect(v).toBeLessThan(120);
  });

  it('Big Bertha with C6-5 outflies the Alpha III on A8-3', () => {
    const alpha = runFullFlight(PRESET_BY_ID['alpha-iii'].rocket, DEFAULT_FLIGHT_CONFIG);
    const bertha = runFullFlight(PRESET_BY_ID['big-bertha'].rocket, DEFAULT_FLIGHT_CONFIG);
    // The heavier, fatter Bertha on a C6-5 should still clear the lighter
    // Alpha III's A8-3 apogee – bigger total impulse wins.
    expect(apogee(bertha)).toBeGreaterThan(apogee(alpha));
  });

  it('two stage explorer transitions through both boost stages', () => {
    const samples = runFullFlight(PRESET_BY_ID['two-stage-explorer'].rocket, DEFAULT_FLIGHT_CONFIG);
    const stage0 = samples.find((s) => s.activeStage === 0 && s.phase === 'boost');
    const stage1 = samples.find((s) => s.activeStage === 1 && s.phase === 'boost');
    expect(stage0).toBeDefined();
    expect(stage1).toBeDefined();
  });
});
