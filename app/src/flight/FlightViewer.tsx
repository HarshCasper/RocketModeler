import { useEffect, useRef } from 'react';
import type { FlightSample, NoseConeShape, Rocket } from '../domain/types';
import { GRAVITY } from '../domain/constants';

interface FlightViewerProps {
  rocket: Rocket;
  sample: FlightSample | null;
  launchAngle: number;
  countdown: number;
  windSpeed: number;
  showForces: boolean;
  dark: boolean;
}

const CANVAS_W = 960;
const CANVAS_H = 640;
const TRAIL_MAX = 400;
const NEAR_SCALE = 6;
const PX_PER_CM = 2.4;

interface SmokeParticle {
  x: number;
  alt: number;
  age: number;
  size: number;
}

interface Cloud {
  x: number; // world m, horizontal
  alt: number; // world m, altitude of cloud band
  w: number;
  h: number;
  speed: number; // drift px per frame
}

interface MilestoneTag {
  text: string;
  age: number;
}

export function FlightViewer({
  rocket,
  sample,
  launchAngle,
  countdown,
  windSpeed,
  showForces,
  dark,
}: FlightViewerProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const trailRef = useRef<{ x: number; altitude: number }[]>([]);
  const lastStageRef = useRef<number | null>(null);
  const stageFlashRef = useRef<number>(0);
  const smokeRef = useRef<SmokeParticle[]>([]);
  const cloudsRef = useRef<Cloud[]>([]);
  const milestoneRef = useRef<MilestoneTag[]>([]);
  const mileFlagsRef = useRef({ mach: false, apogee: false, chute: false });
  const lastAltRef = useRef(0);

  useEffect(() => {
    const cnv = canvasRef.current;
    if (!cnv) return;
    const ctx = cnv.getContext('2d');
    if (!ctx) return;
    if (cloudsRef.current.length === 0) cloudsRef.current = makeClouds();
    if (!sample || sample.phase === 'pad') {
      trailRef.current = [];
      lastStageRef.current = null;
      stageFlashRef.current = 0;
      smokeRef.current = [];
      milestoneRef.current = [];
      mileFlagsRef.current = { mach: false, apogee: false, chute: false };
      lastAltRef.current = 0;
    } else {
      trailRef.current.push({ x: sample.xDistance, altitude: sample.altitude });
      if (trailRef.current.length > TRAIL_MAX) {
        trailRef.current.splice(0, trailRef.current.length - TRAIL_MAX);
      }
      if (lastStageRef.current !== null && lastStageRef.current !== sample.activeStage) {
        stageFlashRef.current = 1;
        milestoneRef.current.push({ text: `Stage ${sample.activeStage + 1} ignition`, age: 0 });
      }
      lastStageRef.current = sample.activeStage;
      stageFlashRef.current = Math.max(0, stageFlashRef.current - 0.04);
      if (sample.phase === 'boost') {
        smokeRef.current.push({
          x: sample.xDistance + (Math.random() - 0.5) * 0.05,
          alt: Math.max(0, sample.altitude - 0.05),
          age: 0,
          size: 5 + Math.random() * 3,
        });
      }
      for (const p of smokeRef.current) {
        p.age += 1;
        p.x += windSpeed * 0.0008;
      }
      smokeRef.current = smokeRef.current.filter((p) => p.age < 110).slice(-200);

      // Milestones, one shot each.
      if (!mileFlagsRef.current.mach && sample.speed > 343) {
        mileFlagsRef.current.mach = true;
        milestoneRef.current.push({ text: `Mach 1 at ${sample.altitude.toFixed(0)} m`, age: 0 });
      }
      if (
        !mileFlagsRef.current.apogee &&
        lastAltRef.current > 1 &&
        sample.altitude < lastAltRef.current
      ) {
        mileFlagsRef.current.apogee = true;
        milestoneRef.current.push({ text: `Apogee ${lastAltRef.current.toFixed(0)} m`, age: 0 });
      }
      if (!mileFlagsRef.current.chute && sample.phase === 'descent') {
        mileFlagsRef.current.chute = true;
        milestoneRef.current.push({ text: 'Parachute deployed', age: 0 });
      }
      lastAltRef.current = Math.max(lastAltRef.current, sample.altitude);
      for (const m of milestoneRef.current) m.age += 1;
      milestoneRef.current = milestoneRef.current.filter((m) => m.age < 70);

      for (const c of cloudsRef.current) {
        c.x += c.speed + windSpeed * 0.01;
      }
    }
    draw(ctx, {
      rocket,
      sample,
      launchAngle,
      countdown,
      windSpeed,
      trail: trailRef.current,
      stageFlash: stageFlashRef.current,
      smoke: smokeRef.current,
      clouds: cloudsRef.current,
      milestones: milestoneRef.current,
      showForces,
      dark,
    });
  }, [rocket, sample, launchAngle, countdown, windSpeed, showForces, dark]);

  return (
    <canvas
      ref={canvasRef}
      width={CANVAS_W}
      height={CANVAS_H}
      className="w-full h-full max-h-[680px] rounded"
      role="img"
      aria-label="Flight visualization"
    />
  );
}

function makeClouds(): Cloud[] {
  const clouds: Cloud[] = [];
  // Two altitude bands so they parallax differently.
  for (let i = 0; i < 6; i++) {
    clouds.push({
      x: -200 + i * 220 + Math.random() * 80,
      alt: 60 + Math.random() * 80,
      w: 110 + Math.random() * 80,
      h: 24 + Math.random() * 10,
      speed: 0.18 + Math.random() * 0.08,
    });
  }
  for (let i = 0; i < 5; i++) {
    clouds.push({
      x: -300 + i * 260 + Math.random() * 100,
      alt: 220 + Math.random() * 80,
      w: 160 + Math.random() * 90,
      h: 30 + Math.random() * 14,
      speed: 0.08 + Math.random() * 0.05,
    });
  }
  return clouds;
}

interface DrawCtx {
  rocket: Rocket;
  sample: FlightSample | null;
  launchAngle: number;
  countdown: number;
  windSpeed: number;
  trail: { x: number; altitude: number }[];
  stageFlash: number;
  smoke: SmokeParticle[];
  clouds: Cloud[];
  milestones: MilestoneTag[];
  showForces: boolean;
  dark: boolean;
}

function draw(ctx: CanvasRenderingContext2D, d: DrawCtx) {
  const W = ctx.canvas.width;
  const H = ctx.canvas.height;
  ctx.clearRect(0, 0, W, H);

  const altitude = d.sample?.altitude ?? 0;
  const skyMix = Math.min(1, altitude / 1800);
  drawSky(ctx, skyMix, d.dark);

  // Camera. Below 60 m the ground stays anchored to the canvas bottom; above
  // that the camera tracks the rocket and the ground scrolls off.
  const groundLineY = H - 80;
  let scale: number;
  let cameraBottomAlt: number;
  if (altitude < 60) {
    scale = NEAR_SCALE;
    cameraBottomAlt = 0;
  } else {
    scale = Math.max(0.45, NEAR_SCALE / (1 + altitude / 220));
    const targetRocketY = d.sample?.phase === 'descent' ? H * 0.55 : H * 0.35;
    cameraBottomAlt = altitude - (groundLineY - targetRocketY) / scale;
  }
  const worldY = (alt: number) => groundLineY - (alt - cameraBottomAlt) * scale;
  const worldX = (x: number) => W / 2 + x * scale;

  drawCloudLayer(ctx, d.clouds, worldY, d.dark);

  // Distant horizon hills (parallax). Visible while ground is on screen.
  const groundOnScreen = worldY(0) < H;
  if (groundOnScreen) drawHills(ctx, worldY, d.dark);

  // Ground band (grass).
  if (groundOnScreen) drawGround(ctx, worldY(0), H, W, d.dark);

  // Launch rod.
  drawRod(ctx, d.launchAngle, worldX(0), worldY(0));

  // Smoke plume.
  for (const p of d.smoke) {
    const px = worldX(p.x);
    const py = worldY(p.alt);
    const alpha = Math.max(0, 0.32 * (1 - p.age / 110));
    const r = p.size * (1 + p.age * 0.018);
    ctx.fillStyle = `rgba(${d.dark ? '220,220,220' : '180,180,180'},${alpha})`;
    ctx.beginPath();
    ctx.arc(px, py, r, 0, Math.PI * 2);
    ctx.fill();
  }

  // Trajectory trail.
  if (d.trail.length > 1) {
    ctx.strokeStyle = d.dark ? 'rgba(140,180,255,0.6)' : 'rgba(11,61,145,0.45)';
    ctx.lineWidth = 1.4;
    ctx.beginPath();
    for (let i = 0; i < d.trail.length; i++) {
      const t = d.trail[i];
      const tx = worldX(t.x);
      const ty = worldY(t.altitude);
      if (i === 0) ctx.moveTo(tx, ty);
      else ctx.lineTo(tx, ty);
    }
    ctx.stroke();
  }

  // Rocket sprite at the computed screen position.
  const rocketScreenX = worldX(d.sample?.xDistance ?? 0);
  const rocketScreenY = worldY(altitude);
  drawRocket(ctx, rocketScreenX, rocketScreenY, d.rocket, d.launchAngle, d.sample);

  if (d.showForces && d.sample && d.sample.phase !== 'pad' && d.sample.phase !== 'landed') {
    drawForceVectors(ctx, rocketScreenX, rocketScreenY, d.rocket, d.sample);
  }

  drawAltitudeAxis(ctx, scale, cameraBottomAlt, groundLineY, H, d.dark);
  drawWindIndicator(ctx, d.windSpeed, d.dark);

  // Milestone tags stacked under the wind chip.
  drawMilestones(ctx, d.milestones, d.dark);

  if (d.stageFlash > 0 && d.sample) {
    ctx.fillStyle = d.dark
      ? `rgba(200,220,255,${d.stageFlash * 0.85})`
      : `rgba(11,61,145,${d.stageFlash * 0.85})`;
    ctx.font = 'bold 22px Inter, system-ui, sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(`STAGE ${d.sample.activeStage + 1} IGNITED`, W / 2, 70);
  }

  if (d.countdown > 0) {
    ctx.fillStyle = '#D63333';
    ctx.font = 'bold 72px JetBrains Mono, ui-monospace, monospace';
    ctx.textAlign = 'center';
    ctx.fillText(`T − ${d.countdown}`, W / 2, 120);
  }

  if (d.sample?.phase === 'crashed') {
    drawCenterBanner(ctx, 'Your rocket was unstable', '#C0392B');
  } else if (d.sample?.phase === 'landed') {
    drawCenterBanner(ctx, 'Touchdown', '#2E8B57');
  }
}

function drawSky(ctx: CanvasRenderingContext2D, mix: number, dark: boolean) {
  const W = ctx.canvas.width;
  const H = ctx.canvas.height;
  const horizonRgb: [number, number, number] = dark
    ? lerpColor([22, 34, 64], [3, 6, 22], mix)
    : lerpColor([200, 220, 240], [11, 40, 102], mix);
  const highRgb: [number, number, number] = dark
    ? lerpColor([8, 14, 32], [0, 0, 8], mix)
    : lerpColor([240, 245, 251], [3, 8, 31], mix);
  const grad = ctx.createLinearGradient(0, 0, 0, H);
  grad.addColorStop(0, rgb(highRgb));
  grad.addColorStop(1, rgb(horizonRgb));
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, W, H);
}

function rgb(c: [number, number, number]): string {
  return `rgb(${c[0]},${c[1]},${c[2]})`;
}

function drawHills(
  ctx: CanvasRenderingContext2D,
  worldY: (alt: number) => number,
  dark: boolean,
) {
  const W = ctx.canvas.width;
  const baseY = worldY(0);
  ctx.fillStyle = dark ? '#1a2b3a' : '#7a98b5';
  ctx.beginPath();
  ctx.moveTo(0, baseY);
  const bumps = 7;
  for (let i = 0; i <= bumps; i++) {
    const x = (i / bumps) * W;
    const peak = 14 + (i % 3) * 6;
    ctx.lineTo(x, baseY - peak);
  }
  ctx.lineTo(W, baseY);
  ctx.closePath();
  ctx.fill();
}

function drawGround(
  ctx: CanvasRenderingContext2D,
  groundY: number,
  H: number,
  W: number,
  dark: boolean,
) {
  const grass = dark ? '#1c3a25' : '#3aa56a';
  const grass2 = dark ? '#0e2716' : '#2c8854';
  const grad = ctx.createLinearGradient(0, groundY, 0, H);
  grad.addColorStop(0, grass);
  grad.addColorStop(1, grass2);
  ctx.fillStyle = grad;
  ctx.fillRect(0, groundY, W, H - groundY);
  // soft blade texture
  ctx.strokeStyle = dark ? 'rgba(110,180,140,0.35)' : 'rgba(30,80,50,0.55)';
  ctx.lineWidth = 1;
  for (let x = 0; x < W; x += 14) {
    const jitter = ((x * 31) % 11) - 5;
    ctx.beginPath();
    ctx.moveTo(x, groundY);
    ctx.lineTo(x + 3 + jitter / 3, groundY - 5);
    ctx.stroke();
  }
}

function drawRod(
  ctx: CanvasRenderingContext2D,
  launchAngle: number,
  padX: number,
  padY: number,
) {
  const rodLengthPx = 64;
  const a = (launchAngle * Math.PI) / 180;
  const baseX = padX - 16;
  const tipX = baseX + rodLengthPx * Math.cos(a);
  const tipY = padY - rodLengthPx * Math.sin(a);
  ctx.strokeStyle = '#2a2a2a';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.moveTo(baseX, padY);
  ctx.lineTo(tipX, tipY);
  ctx.stroke();
  // base block
  ctx.fillStyle = '#3a3a3a';
  ctx.fillRect(baseX - 8, padY - 4, 16, 6);
}

function drawCloudLayer(
  ctx: CanvasRenderingContext2D,
  clouds: Cloud[],
  worldY: (alt: number) => number,
  dark: boolean,
) {
  for (const c of clouds) {
    const y = worldY(c.alt);
    if (y < -50 || y > ctx.canvas.height + 50) continue;
    const x = ((c.x % 1100) + 1500) % 1500 - 200;
    ctx.fillStyle = dark ? 'rgba(120,140,180,0.32)' : 'rgba(255,255,255,0.65)';
    ctx.beginPath();
    ctx.ellipse(x, y, c.w / 2, c.h / 2, 0, 0, Math.PI * 2);
    ctx.ellipse(x + c.w * 0.25, y - 4, c.w / 3, c.h / 2.4, 0, 0, Math.PI * 2);
    ctx.ellipse(x - c.w * 0.25, y - 2, c.w / 3.2, c.h / 2.6, 0, 0, Math.PI * 2);
    ctx.fill();
  }
}

function drawAltitudeAxis(
  ctx: CanvasRenderingContext2D,
  scale: number,
  cameraBottomAlt: number,
  groundLineY: number,
  H: number,
  dark: boolean,
) {
  ctx.fillStyle = dark ? 'rgba(220,232,255,0.55)' : 'rgba(11,19,32,0.55)';
  ctx.strokeStyle = dark ? 'rgba(220,232,255,0.22)' : 'rgba(11,19,32,0.18)';
  ctx.lineWidth = 1;
  ctx.font = '11px JetBrains Mono, ui-monospace, monospace';
  ctx.textAlign = 'left';
  const visibleRange = H / scale;
  let tickEvery = 10;
  if (visibleRange > 80) tickEvery = 25;
  if (visibleRange > 200) tickEvery = 50;
  if (visibleRange > 500) tickEvery = 100;
  if (visibleRange > 1500) tickEvery = 250;
  const startAlt = Math.max(0, Math.floor(cameraBottomAlt / tickEvery) * tickEvery);
  for (let h = startAlt; h < cameraBottomAlt + visibleRange; h += tickEvery) {
    const y = groundLineY - (h - cameraBottomAlt) * scale;
    if (y < 0 || y > H) continue;
    ctx.beginPath();
    ctx.moveTo(6, y);
    ctx.lineTo(28, y);
    ctx.stroke();
    ctx.fillText(`${h} m`, 32, y + 3);
  }
}

function drawWindIndicator(ctx: CanvasRenderingContext2D, windSpeed: number, dark: boolean) {
  const W = ctx.canvas.width;
  const padding = 14;
  const cx = W - 100;
  const cy = padding + 22;
  ctx.fillStyle = dark ? 'rgba(8,14,32,0.78)' : 'rgba(255,255,255,0.85)';
  ctx.strokeStyle = dark ? 'rgba(120,150,200,0.4)' : 'rgba(11,61,145,0.25)';
  ctx.lineWidth = 1;
  const boxW = 130;
  const boxH = 42;
  roundRect(ctx, cx - boxW / 2, cy - boxH / 2, boxW, boxH, 6);
  ctx.fill();
  ctx.stroke();
  const labelColor = dark ? 'rgba(200,215,240,0.65)' : 'rgba(11,19,32,0.6)';
  const valueColor = dark ? '#dbe7ff' : '#0B1320';
  ctx.fillStyle = labelColor;
  ctx.font = '9px JetBrains Mono, ui-monospace, monospace';
  ctx.textAlign = 'left';
  ctx.fillText('WIND', cx - boxW / 2 + 8, cy - 7);
  ctx.fillStyle = valueColor;
  ctx.font = '11px JetBrains Mono, ui-monospace, monospace';
  ctx.fillText(`${Math.abs(windSpeed).toFixed(1)} m/s`, cx - boxW / 2 + 8, cy + 9);
  const maxMag = 10;
  const mag = Math.min(Math.abs(windSpeed) / maxMag, 1);
  const arrowLen = 32 * mag;
  const arrowX = cx + 22;
  const arrowY = cy + 1;
  const dir = windSpeed >= 0 ? 1 : -1;
  ctx.strokeStyle = windSpeed === 0
    ? dark
      ? 'rgba(200,215,240,0.25)'
      : 'rgba(11,19,32,0.25)'
    : dark
      ? '#7AA8FF'
      : '#0B3D91';
  ctx.fillStyle = ctx.strokeStyle;
  ctx.lineWidth = 1.6;
  ctx.beginPath();
  ctx.moveTo(arrowX, arrowY);
  ctx.lineTo(arrowX + dir * arrowLen, arrowY);
  ctx.stroke();
  if (arrowLen > 2) {
    const tipX = arrowX + dir * arrowLen;
    ctx.beginPath();
    ctx.moveTo(tipX, arrowY);
    ctx.lineTo(tipX - dir * 6, arrowY - 4);
    ctx.lineTo(tipX - dir * 6, arrowY + 4);
    ctx.closePath();
    ctx.fill();
  }
}

function drawMilestones(
  ctx: CanvasRenderingContext2D,
  tags: MilestoneTag[],
  dark: boolean,
) {
  const W = ctx.canvas.width;
  const startY = 70;
  for (let i = 0; i < tags.length; i++) {
    const m = tags[i];
    const alpha = m.age < 10 ? m.age / 10 : Math.max(0, 1 - (m.age - 10) / 60);
    if (alpha <= 0) continue;
    const w = 220;
    const h = 26;
    const x = W - 100 - w / 2;
    const y = startY + i * 30;
    ctx.fillStyle = dark
      ? `rgba(20,32,64,${alpha * 0.92})`
      : `rgba(255,255,255,${alpha * 0.92})`;
    ctx.strokeStyle = dark
      ? `rgba(120,160,255,${alpha * 0.6})`
      : `rgba(11,61,145,${alpha * 0.45})`;
    ctx.lineWidth = 1;
    roundRect(ctx, x, y, w, h, 5);
    ctx.fill();
    ctx.stroke();
    ctx.fillStyle = dark ? `rgba(220,232,255,${alpha})` : `rgba(11,19,32,${alpha})`;
    ctx.font = '11px Inter, system-ui, sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(m.text, x + w / 2, y + 16);
  }
}

function drawCenterBanner(ctx: CanvasRenderingContext2D, text: string, color: string) {
  const W = ctx.canvas.width;
  const H = ctx.canvas.height;
  ctx.fillStyle = color;
  ctx.globalAlpha = 0.92;
  ctx.fillRect(0, H / 2 - 24, W, 48);
  ctx.globalAlpha = 1;
  ctx.fillStyle = 'white';
  ctx.font = 'bold 18px Inter, system-ui, sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText(text, W / 2, H / 2 + 6);
}

function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  w: number,
  h: number,
  r: number,
) {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.lineTo(x + w - r, y);
  ctx.quadraticCurveTo(x + w, y, x + w, y + r);
  ctx.lineTo(x + w, y + h - r);
  ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
  ctx.lineTo(x + r, y + h);
  ctx.quadraticCurveTo(x, y + h, x, y + h - r);
  ctx.lineTo(x, y + r);
  ctx.quadraticCurveTo(x, y, x + r, y);
  ctx.closePath();
}

function drawRocket(
  ctx: CanvasRenderingContext2D,
  cx: number,
  cy: number,
  rocket: Rocket,
  launchAngleDeg: number,
  sample: FlightSample | null,
) {
  // Real cm to pixels for the sprite, then a small minimum so very fat
  // rockets render with visible features.
  const bodyLen = Math.max(48, rocket.body.length * PX_PER_CM);
  const bodyW = Math.max(10, rocket.body.diameter * PX_PER_CM);
  const noseLen = Math.max(14, rocket.noseCone.length * PX_PER_CM);
  const finLen = Math.max(14, rocket.fins.length * PX_PER_CM);
  const finW = Math.max(8, rocket.fins.width * PX_PER_CM);
  const finH = rocket.fins.height * PX_PER_CM;

  let tiltDeg = sample?.tiltDeg ?? launchAngleDeg;
  if (!sample || sample.phase === 'pad') tiltDeg = launchAngleDeg;
  if (sample?.phase === 'descent') tiltDeg = 90;

  ctx.save();
  ctx.translate(cx, cy);
  const rot = ((90 - tiltDeg) * Math.PI) / 180;
  ctx.rotate(rot);

  // Body tube with vertical gradient for a subtle metallic shading.
  const bodyGrad = ctx.createLinearGradient(-bodyW / 2, 0, bodyW / 2, 0);
  bodyGrad.addColorStop(0, '#2A6BB8');
  bodyGrad.addColorStop(0.5, '#5AA0E8');
  bodyGrad.addColorStop(1, '#2A6BB8');
  ctx.fillStyle = bodyGrad;
  ctx.strokeStyle = '#0F1B2F';
  ctx.lineWidth = 1.2;
  ctx.fillRect(-bodyW / 2, -bodyLen / 2, bodyW, bodyLen);
  ctx.strokeRect(-bodyW / 2, -bodyLen / 2, bodyW, bodyLen);

  // Panel lines suggest construction.
  ctx.strokeStyle = 'rgba(15,27,47,0.35)';
  ctx.lineWidth = 0.7;
  for (let i = 1; i < 4; i++) {
    const y = -bodyLen / 2 + (i * bodyLen) / 4;
    ctx.beginPath();
    ctx.moveTo(-bodyW / 2 + 1, y);
    ctx.lineTo(bodyW / 2 - 1, y);
    ctx.stroke();
  }

  // Launch lug (small tube on the side of body).
  ctx.fillStyle = '#1A1A1A';
  ctx.fillRect(bodyW / 2, bodyLen / 6, 2, bodyLen / 4);

  // Nose cone (selected shape) with a slight inner highlight.
  ctx.fillStyle = '#C42A2A';
  ctx.strokeStyle = '#0F1B2F';
  ctx.lineWidth = 1.2;
  traceNoseConeShape(ctx, rocket.noseCone.shape ?? 'cone', bodyW, bodyLen, noseLen);
  ctx.fill();
  ctx.stroke();
  // Highlight
  ctx.fillStyle = 'rgba(255,255,255,0.18)';
  traceNoseConeShape(ctx, rocket.noseCone.shape ?? 'cone', bodyW * 0.6, bodyLen, noseLen * 0.95);
  ctx.fill();

  // Fins (triangular).
  const finBottomY = bodyLen / 2 - finH;
  const finTopY = finBottomY - finLen;
  const finFill = '#181818';
  ctx.fillStyle = finFill;
  ctx.strokeStyle = '#0F1B2F';
  ctx.lineWidth = 0.8;
  for (const side of [-1, 1]) {
    ctx.beginPath();
    ctx.moveTo((side * bodyW) / 2, finBottomY);
    ctx.lineTo((side * bodyW) / 2 + side * finW, finBottomY);
    ctx.lineTo((side * bodyW) / 2, finTopY);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
  }
  // center fin hint (the fin behind the body)
  ctx.strokeStyle = 'rgba(20,20,20,0.6)';
  ctx.lineWidth = 1;
  ctx.beginPath();
  ctx.moveTo(0, finBottomY);
  ctx.lineTo(0, finTopY);
  ctx.stroke();

  // Exhaust during boost.
  if (sample?.phase === 'boost') {
    const baseY = bodyLen / 2;
    const flicker = 0.6 + Math.random() * 0.4;
    const flameLen = (14 + Math.random() * 8) * flicker;
    const flameW = bodyW * 0.7;
    ctx.fillStyle = 'rgba(214,51,51,0.92)';
    ctx.beginPath();
    ctx.moveTo(-flameW / 2, baseY);
    ctx.quadraticCurveTo(0, baseY + flameLen * 0.6, flameW / 2, baseY);
    ctx.lineTo(0, baseY + flameLen);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = 'rgba(255,210,60,0.95)';
    ctx.beginPath();
    ctx.moveTo(-flameW / 4, baseY);
    ctx.lineTo(0, baseY + flameLen * 0.75);
    ctx.lineTo(flameW / 4, baseY);
    ctx.closePath();
    ctx.fill();
    ctx.fillStyle = 'rgba(255,255,255,0.88)';
    for (let i = 0; i < 5; i++) {
      const sx = (Math.random() - 0.5) * flameW;
      const sy = baseY + Math.random() * flameLen * 1.2;
      ctx.fillRect(sx, sy, 1.5, 1.5);
    }
  }

  // Parachute. Cap visual size against the body so it never dwarfs the
  // viewport, and skip drawing if the rocket is so high the chute would clip
  // above the canvas (we instead show a smaller schematic).
  if (sample?.phase === 'descent') {
    const rawChuteR = rocket.parachuteDiameter * 100 * PX_PER_CM * 0.55;
    const chuteR = Math.min(rawChuteR, bodyLen * 1.6);
    const chuteCY = -bodyLen / 2 - noseLen - chuteR / 2;
    const chuteRY = chuteR / 1.8;
    ctx.fillStyle = 'rgba(232,87,87,0.78)';
    ctx.strokeStyle = '#0F1B2F';
    ctx.lineWidth = 1.4;
    ctx.beginPath();
    ctx.ellipse(0, chuteCY, chuteR, chuteRY, 0, Math.PI, 0);
    ctx.fill();
    ctx.stroke();
    // Alternating canopy panel stripes for depth.
    ctx.strokeStyle = 'rgba(15,27,47,0.45)';
    ctx.lineWidth = 0.9;
    for (let i = 1; i < 5; i++) {
      const angle = Math.PI - (i * Math.PI) / 5;
      const ex = chuteR * Math.cos(angle);
      const ey = chuteCY + chuteRY * Math.sin(angle);
      ctx.beginPath();
      ctx.moveTo(0, chuteCY);
      ctx.lineTo(ex, ey);
      ctx.stroke();
    }
    // Shroud lines.
    ctx.strokeStyle = '#0F1B2F';
    ctx.lineWidth = 1;
    for (const k of [-0.85, -0.4, 0.4, 0.85]) {
      const sx = chuteR * k;
      const sy = chuteCY + Math.abs(k) * chuteRY * 0.4;
      ctx.beginPath();
      ctx.moveTo(sx, sy);
      ctx.lineTo(0, -bodyLen / 2);
      ctx.stroke();
    }
  }

  ctx.restore();
}

function drawForceVectors(
  ctx: CanvasRenderingContext2D,
  cx: number,
  cy: number,
  _rocket: Rocket,
  sample: FlightSample,
) {
  // Vectors in m/s^2 (acceleration components). Map to a comfortable arrow
  // length on screen so the largest vector is roughly 90 px.
  const massKg = sample.mass / 1000;
  const tiltRad = (sample.tiltDeg * Math.PI) / 180;
  const ax = (sample.thrust * Math.cos(tiltRad)) / Math.max(massKg, 0.001);
  const ay = (sample.thrust * Math.sin(tiltRad)) / Math.max(massKg, 0.001);
  const speedMag = Math.max(0.001, sample.speed);
  // Estimate drag accel via net minus thrust+gravity.
  const dragAx = -ax + (sample.vx * 0); // placeholder; we don't have drag broken out
  void dragAx;
  const maxMag = Math.max(50, Math.abs(GRAVITY), Math.hypot(ax, ay));
  const px = 90 / maxMag;

  // Thrust (red).
  drawArrow(ctx, cx, cy, cx + ax * px, cy - ay * px, '#D63333', 'thrust');
  // Gravity (gray, down).
  drawArrow(ctx, cx, cy, cx, cy + GRAVITY * px, '#666', 'gravity');
  // Velocity vector (blue, scaled differently).
  const vpx = 90 / Math.max(speedMag, 1);
  drawArrow(
    ctx,
    cx,
    cy,
    cx + sample.vx * vpx,
    cy - sample.vy * vpx,
    '#0B3D91',
    `${sample.speed.toFixed(0)} m/s`,
  );
}

function drawArrow(
  ctx: CanvasRenderingContext2D,
  x1: number,
  y1: number,
  x2: number,
  y2: number,
  color: string,
  label: string,
) {
  const dx = x2 - x1;
  const dy = y2 - y1;
  const len = Math.hypot(dx, dy);
  if (len < 6) return;
  ctx.strokeStyle = color;
  ctx.fillStyle = color;
  ctx.lineWidth = 1.8;
  ctx.beginPath();
  ctx.moveTo(x1, y1);
  ctx.lineTo(x2, y2);
  ctx.stroke();
  const ang = Math.atan2(dy, dx);
  ctx.beginPath();
  ctx.moveTo(x2, y2);
  ctx.lineTo(x2 - 9 * Math.cos(ang - 0.4), y2 - 9 * Math.sin(ang - 0.4));
  ctx.lineTo(x2 - 9 * Math.cos(ang + 0.4), y2 - 9 * Math.sin(ang + 0.4));
  ctx.closePath();
  ctx.fill();
  ctx.font = '10px Inter, system-ui, sans-serif';
  ctx.textAlign = 'left';
  ctx.fillText(label, x2 + 4, y2 + 4);
}

function traceNoseConeShape(
  ctx: CanvasRenderingContext2D,
  shape: NoseConeShape,
  bodyW: number,
  bodyLen: number,
  noseLen: number,
) {
  const baseY = -bodyLen / 2;
  const tipY = baseY - noseLen;
  const halfW = bodyW / 2;
  ctx.beginPath();
  switch (shape) {
    case 'ogive': {
      const K = 0.55;
      const baseCtrlY = baseY - K * noseLen;
      const tipCtrlOffset = K * halfW;
      ctx.moveTo(-halfW, baseY);
      ctx.bezierCurveTo(-halfW, baseCtrlY, -tipCtrlOffset, tipY, 0, tipY);
      ctx.bezierCurveTo(tipCtrlOffset, tipY, halfW, baseCtrlY, halfW, baseY);
      break;
    }
    case 'parabolic': {
      ctx.moveTo(-halfW, baseY);
      ctx.quadraticCurveTo(0, baseY - 2 * noseLen, halfW, baseY);
      break;
    }
    case 'elliptical': {
      ctx.moveTo(-halfW, baseY);
      ctx.ellipse(0, baseY, halfW, noseLen, 0, Math.PI, 0, true);
      break;
    }
    case 'cone':
    default:
      ctx.moveTo(-halfW, baseY);
      ctx.lineTo(0, tipY);
      ctx.lineTo(halfW, baseY);
      break;
  }
  ctx.closePath();
}

function lerpColor(
  a: [number, number, number],
  b: [number, number, number],
  t: number,
): [number, number, number] {
  return [
    Math.round(a[0] + (b[0] - a[0]) * t),
    Math.round(a[1] + (b[1] - a[1]) * t),
    Math.round(a[2] + (b[2] - a[2]) * t),
  ];
}
