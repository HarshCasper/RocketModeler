import { describe, expect, it } from 'vitest';
import { runFullFlight, apogee, peakSpeed } from '../simulate';
import { DEFAULT_ROCKET, DEFAULT_FLIGHT_CONFIG } from '../../domain/defaults';

describe('flight integrator', () => {
  it('default rocket (A8-3, vertical launch) reaches a plausible apogee', () => {
    const samples = runFullFlight(DEFAULT_ROCKET, DEFAULT_FLIGHT_CONFIG);
    const a = apogee(samples);
    // A8-3 on a small Alpha-style rocket peaks somewhere in ~30-120 m. We just
    // want to know it left the pad and reached something realistic.
    expect(a).toBeGreaterThan(30);
    expect(a).toBeLessThan(250);
  });

  it('peak velocity is positive and finite', () => {
    const samples = runFullFlight(DEFAULT_ROCKET, DEFAULT_FLIGHT_CONFIG);
    const v = peakSpeed(samples);
    expect(v).toBeGreaterThan(10);
    expect(v).toBeLessThan(200);
  });

  it('rocket lands (alt comes back to 0)', () => {
    const samples = runFullFlight(DEFAULT_ROCKET, DEFAULT_FLIGHT_CONFIG);
    const last = samples[samples.length - 1];
    expect(last.altitude).toBeLessThanOrEqual(0.5);
    expect(['landed', 'crashed']).toContain(last.phase);
  });
});
