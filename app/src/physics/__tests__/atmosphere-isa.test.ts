import { describe, expect, it } from 'vitest';
import { airDensity, airTemperatureK } from '../atmosphere-isa';

describe('ISA atmosphere', () => {
  it('sea-level density is ~1.225 kg/m^3', () => {
    expect(airDensity(0)).toBeCloseTo(1.225, 3);
  });

  it('density at 1000 m is ~1.1117 kg/m^3 (ISA table)', () => {
    expect(airDensity(1000)).toBeCloseTo(1.1117, 2);
  });

  it('density at 5000 m is ~0.7364 kg/m^3 (ISA table)', () => {
    expect(airDensity(5000)).toBeCloseTo(0.7364, 2);
  });

  it('density monotonically decreases with altitude', () => {
    let prev = airDensity(0);
    for (let h = 100; h <= 10000; h += 200) {
      const d = airDensity(h);
      expect(d).toBeLessThan(prev);
      prev = d;
    }
  });

  it('sea-level temperature is 288.15 K', () => {
    expect(airTemperatureK(0)).toBeCloseTo(288.15, 2);
  });
});
