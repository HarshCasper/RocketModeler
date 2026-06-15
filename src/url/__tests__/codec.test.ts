import { describe, expect, it } from 'vitest';
import { decodeRocket, encodeRocket } from '../codec';
import { DEFAULT_ROCKET } from '../../domain/defaults';
import { PRESETS } from '../../domain/presets';

describe('url hash codec', () => {
  it('round trips the default rocket', () => {
    const enc = encodeRocket(DEFAULT_ROCKET);
    expect(enc.startsWith('#r=')).toBe(true);
    const back = decodeRocket(enc);
    expect(back).toEqual(DEFAULT_ROCKET);
  });

  it('preserves the nose cone shape across round trips', () => {
    for (const shape of ['cone', 'ogive', 'parabolic', 'elliptical'] as const) {
      const r = {
        ...DEFAULT_ROCKET,
        noseCone: { ...DEFAULT_ROCKET.noseCone, shape },
      };
      const back = decodeRocket(encodeRocket(r));
      expect(back?.noseCone.shape).toBe(shape);
    }
  });

  it('round trips every preset', () => {
    for (const p of PRESETS) {
      const back = decodeRocket(encodeRocket(p.rocket));
      expect(back, p.name).toEqual(p.rocket);
    }
  });

  it('returns null for malformed input', () => {
    expect(decodeRocket('#r=not-real-data')).toBeNull();
    expect(decodeRocket('no prefix')).toBeNull();
    expect(decodeRocket('#r=')).toBeNull();
  });
});
