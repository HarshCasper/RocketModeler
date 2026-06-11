# RocketModeler

A modern web rebuild of NASA Glenn Research Center's RocketModeler Java applet (Eric Bishop, OSU / NASA GRC, c. 2002).

The original applet ran in browsers via the Java Plugin, which has been gone for years. This is an independent reimplementation in TypeScript + React, targeting the same pedagogical experience — design a model rocket, see its CG and CP shift in real time, then launch it and watch the altitude tick up — with quietly upgraded physics underneath.

This project is **not** affiliated with or endorsed by NASA. The original applet is in the public domain as US government work.

## Features

- **Design mode** with body, nose-cone and fin geometry sliders, materials picker (balsa / plastic / hollow plastic / custom), 1–3 stages, 3 or 4 fins, and **selectable nose cone shapes** (cone / ogive / parabolic / elliptical) with shape-aware Barrowman terms and typical-Cd suggestions.
- **Hover-reveal drag handles** on the SVG diagram for direct manipulation of nose length, body length, fin length and fin width.
- **Curated engine catalog** with Estes motors from 1/2A through F (E9, E12, F15 sustainers), each with an inline thrust-curve mini-chart and total-impulse readout.
- **Preset rocket library** — Estes Alpha III, Big Bertha, and a two-stage explorer, one click away.
- **Live CG / CP / mass readout** with stability caliber gauge — green / yellow / red zones, threshold tuned to hobby conventions (1.0–2.5 cal).
- **Multi-stage CG inspector** — view CG/CP for the full rocket, top N stages, or just the top stage (the original applet's sneakiest pedagogical feature).
- **Flight mode** with Canvas2D viewer: real-time integrator at 100 sub-steps per frame, ISA atmosphere, launch-rod constraint, aero-stability gravity turn (weathercocking emerges from wind + margin), trajectory trail, wind direction indicator, parachute deploy, particle exhaust, sky gradient that deepens with altitude.
- **HUD with live g-force, in-flight margin recompute, and on-rod / phase indicator.**
- **Post-flight summary** with altitude / speed / acceleration mini-charts, peak g, and key stats.
- **Share-by-URL** — every design edit gzips into the URL hash, so the address bar is always a shareable snapshot.
- **PNG export** of the rocket diagram from the design viewer.
- **Keyboard shortcuts** — `D` design, `L` launch, `Space` start/pause, `R` reset, `?` about.
- **Opt-in audio** — synthesized 3-2-1 countdown beep, thruster rumble, plus stage-drop and parachute-deploy cues, all via Web Audio (no .au files needed).

## Repo layout

```
/
├── RocketModeler/   ← legacy applet, kept for reference (untouched)
├── SPEC.md          ← design spec for the modern rebuild
├── app/             ← the new web app (Vite + React + TS)
└── README.md
```

## Run it

```bash
cd app
npm install
npm run dev      # vite dev server on http://localhost:5173
npm test         # vitest physics + url-codec suite (32 tests)
npm run build    # production static bundle in app/dist
```

## Physics

- **CG** follows the original applet's mass-weighted port (cone, body tube, fins, engines, payload) and is now recomputed in flight as fuel burns down and stages drop.
- **CP** uses Barrowman with shape-dependent nose terms (Cone 0.667 L, Ogive 0.466 L, Parabolic 0.5 L, Elliptical 0.333 L) and the triangular-fin closed form with body-fin interference (`K_fb`).
- **Atmosphere** is the ISA troposphere model — `ρ(h) = ρ₀ · (1 − L·h/T₀)^((g·M/R·L)−1)` up to the tropopause.
- **Flight integration** is semi-Euler with 100 sub-steps per Δt of 0.045 s, plus a launch-rod axis-projection constraint until the rocket clears 110 cm, and an aerodynamic heading-alignment term that produces gravity-turn and weathercocking from wind + stability margin.

See `app/src/physics/` and `SPEC.md` for the per-module breakdown and reference formulae.

## Credits

- Original RocketModeler applet: Eric Bishop, Ohio State University / NASA Glenn Research Center.
- Modern rebuild: Harsh Mishra.
- License: MIT (see LICENSE).
