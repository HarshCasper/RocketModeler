import type { Rocket } from './types';

// A small library of well-known hobby rockets. Geometry sourced from Estes
// published specs (body length, diameter, nose length, fin chord/span). Fin
// material defaults to balsa unless noted.
export interface RocketPreset {
  id: string;
  name: string;
  blurb: string;
  rocket: Rocket;
}

export const PRESETS: RocketPreset[] = [
  {
    id: 'alpha-iii',
    name: 'Estes Alpha III',
    blurb: 'The classic single-stage starter – a familiar profile from the orange-cap kit.',
    rocket: {
      schemaVersion: 1,
      numStages: 1,
      finCount: 3,
      noseCone: { length: 5.7, materialId: 'plastic', shape: 'cone' },
      body: { length: 17.5, diameter: 2.45 },
      fins: {
        length: 5.1,
        width: 3.81,
        height: 0,
        materialId: 'balsa',
        thicknessInches: 0.125,
      },
      engineIds: ['A8-3'],
      recoveryPayloadMass: 4,
      parachuteDiameter: 0.3,
      dragCoefficient: 0.6,
    },
  },
  {
    id: 'big-bertha',
    name: 'Estes Big Bertha',
    blurb: 'The famously chunky 41 mm tube – soft launches and big parachute drops.',
    rocket: {
      schemaVersion: 1,
      numStages: 1,
      finCount: 4,
      noseCone: { length: 7.6, materialId: 'plastic', shape: 'ogive' },
      body: { length: 47.0, diameter: 4.1 },
      fins: {
        length: 8.9,
        width: 6.4,
        height: 0,
        materialId: 'balsa',
        thicknessInches: 0.125,
      },
      engineIds: ['C6-5'],
      recoveryPayloadMass: 18,
      parachuteDiameter: 0.6,
      dragCoefficient: 0.5,
    },
  },
  {
    id: 'two-stage-explorer',
    name: 'Two-stage explorer',
    blurb: 'Booster + sustainer – watch the staged ignition redraw stability mid-flight.',
    rocket: {
      schemaVersion: 1,
      numStages: 2,
      finCount: 4,
      noseCone: { length: 9.0, materialId: 'plastic', shape: 'ogive' },
      body: { length: 40.0, diameter: 2.5 },
      fins: {
        length: 8.0,
        width: 5.0,
        height: 0,
        materialId: 'balsa',
        thicknessInches: 0.125,
      },
      engineIds: ['B6-0', 'A8-3'],
      recoveryPayloadMass: 12,
      parachuteDiameter: 0.3,
      dragCoefficient: 0.65,
    },
  },
];

export const PRESET_BY_ID: Record<string, RocketPreset> = Object.fromEntries(
  PRESETS.map((p) => [p.id, p]),
);
