# RocketModeler

RocketModeler is a browser version of the NASA Glenn Research Center model
rocket applet that Eric Bishop wrote in Java around 2002. The Java plugin
that ran the original has been gone for years, so this is a TypeScript and
React rebuild that keeps the same design loop: build a paper rocket, watch
how its centre of gravity and centre of pressure move as you tweak it,
then put it on the pad and fly it.

This project is not affiliated with NASA. The original applet is in the
public domain as US Government work.

## What you can do

Design view: drag handles on the rocket SVG let you change nose length,
body length and the fins by hand. Sliders cover the same parameters plus
fin count, stage count, materials, drag coefficient, recovery payload
mass and parachute size. You can pick a nose shape from cone, ogive,
parabolic or elliptical, and the Barrowman CP recomputes for the chosen
shape. A small library of preset rockets (Estes Alpha III, Big Bertha and
a two-stage explorer) is one click away. Each engine in the catalog shows
its thrust curve and total impulse inline. The stability gauge expands to
fit overstable designs instead of pegging the right edge.

Launch view: a Canvas2D scene that follows the rocket up. There is a
launch rod constraint on the way up, a heading alignment term that
produces gravity turn and weathercocking from wind plus stability margin,
and a live recompute of CG, CP and margin as the stages drop. The HUD
shows time, altitude, speed, g-force, mass, stage, current margin and
phase. A trail traces the actual trajectory, a smoke plume drifts in the
wind, and Mach 1, apogee and parachute deployment fire as quiet caption
banners with timestamps. Force vectors for thrust, gravity and velocity
can be turned on from the controls panel.

After landing, the summary modal lists apogee, peak speed, peak g, time
to apogee and total flight time. Altitude, speed and acceleration are
plotted as small charts. A replay button plays the flight back at
slow-motion with a scrub bar so you can stop on a specific moment.

Shortcuts: `D` design, `L` launch, `Space` to launch and pause, `R` to
reset, `?` for the about dialog.

The address bar always contains a shareable snapshot of the current
rocket. Designs round-trip through gzip and base64url so the hash stays
short. Last session is also kept in `localStorage`, with a dark mode
preference saved alongside it.

## Run it

```bash
cd app
npm install
npm run dev      # vite dev server on http://localhost:5173
npm test         # vitest physics and url-codec suite
npm run build    # production static bundle in app/dist
```

## Physics, in one screen

* CG follows the original applet's mass-weighted calculation across nose
  cone, body tube, fins, engines and payload. The same calculation runs
  in flight, so the CG you see in the HUD shifts with fuel burn and stage
  drops.
* CP uses Barrowman with shape-dependent nose terms. The X coefficient is
  0.667 L for a cone, 0.466 L for an ogive, 0.5 L for a parabolic and
  0.333 L for an elliptical nose. The fin term is the closed form for
  triangular delta fins with body-fin interference `K_fb = 1 + R/(s+R)`.
* Atmosphere is the ISA troposphere model, valid up to 11 km.
* Flight integration is semi-Euler at 100 sub-steps per visible frame
  (Δt = 0.045 s). While the rocket is on the launch rod, motion is
  projected onto the rod axis. Once it clears the rod, an aerodynamic
  alignment term turns the heading toward the relative wind, with
  stiffness scaled by stability margin and dynamic pressure. That term
  is what makes the rocket gravity-turn and weathercock without doing
  full moment-of-inertia integration.

Everything lives under `app/src/physics/`. See `SPEC.md` for the original
design contract.

## Credits

* Eric Bishop wrote the original RocketModeler at Ohio State University
  while working with NASA Glenn Research Center.
* This browser rebuild was written by Harsh Mishra.
* MIT licensed. See `LICENSE`.
