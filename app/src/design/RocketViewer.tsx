import { useMemo, useRef, useState } from 'react';
import { motion } from 'framer-motion';
import { useAppStore } from '../state/store';
import { computeStageCg } from '../physics/cg';
import { computeCpForRocket } from '../physics/cp-barrowman';
import { getEngine } from '../domain/engines';
import type { NoseConeShape } from '../domain/types';

type DragField = 'bodyLength' | 'noseLength' | 'finWidth' | 'finLength';

interface DragStart {
  field: DragField;
  startScreenX: number;
  startScreenY: number;
  startValue: number;
}

const FIELD_LIMITS: Record<DragField, { min: number; max: number }> = {
  bodyLength: { min: 10, max: 100 },
  noseLength: { min: 4, max: 30 },
  finWidth: { min: 2, max: 20 },
  finLength: { min: 4, max: 50 },
};

export function noseConePath(
  shape: NoseConeShape,
  leftX: number,
  rightX: number,
  baseY: number,
  tipY: number,
): string {
  const centerX = (leftX + rightX) / 2;
  const noseLen = baseY - tipY;
  const halfWidth = (rightX - leftX) / 2 || 0.0001;
  switch (shape) {
    case 'ogive': {
      // Cubic Bezier approximation of a tangent ogive: vertical tangent at the
      // base (control1 directly above), horizontal tangent at the tip (control2
      // on the tip's horizontal line). Avoids degenerate SVG arc behaviour for
      // tall, thin noses where the true ogive radius is enormous.
      const K = 0.55;
      const baseCtrlY = baseY - K * noseLen;
      const tipCtrlOffset = K * halfWidth;
      return (
        `M ${leftX} ${baseY} ` +
        `C ${leftX} ${baseCtrlY} ${centerX - tipCtrlOffset} ${tipY} ${centerX} ${tipY} ` +
        `C ${centerX + tipCtrlOffset} ${tipY} ${rightX} ${baseCtrlY} ${rightX} ${baseY} Z`
      );
    }
    case 'parabolic': {
      const ctrlY = baseY - 2 * noseLen;
      return `M ${leftX} ${baseY} Q ${centerX} ${ctrlY} ${rightX} ${baseY} Z`;
    }
    case 'elliptical':
      // sweep-flag = 0 traces the top half of the ellipse (toward the tip).
      return `M ${leftX} ${baseY} A ${halfWidth} ${noseLen} 0 0 0 ${rightX} ${baseY} Z`;
    case 'cone':
    default:
      return `M ${leftX} ${baseY} L ${centerX} ${tipY} L ${rightX} ${baseY} Z`;
  }
}

// SVG renders the rocket side-on. Internal "world" is in cm; we map to SVG
// units with a scale factor and let the SVG viewBox handle responsiveness.
const PIXELS_PER_CM = 6;
const VIEW_WIDTH = 360;
const VIEW_HEIGHT = 460;
const BASELINE_Y = VIEW_HEIGHT - 30;

export function RocketViewer() {
  const rocket = useAppStore((s) => s.rocket);
  const stagesShowing = useAppStore((s) => s.stagesShowing);
  const updateRocket = useAppStore((s) => s.updateRocket);
  const svgRef = useRef<SVGSVGElement>(null);
  const dragRef = useRef<DragStart | null>(null);
  const [dragging, setDragging] = useState<DragField | null>(null);

  const { cg } = useMemo(() => computeStageCg(rocket, stagesShowing), [rocket, stagesShowing]);
  const { cp } = useMemo(() => computeCpForRocket(rocket), [rocket]);

  function startDrag(field: DragField, e: React.PointerEvent<SVGElement>) {
    e.preventDefault();
    e.stopPropagation();
    e.currentTarget.setPointerCapture(e.pointerId);
    const start: DragStart = {
      field,
      startScreenX: e.clientX,
      startScreenY: e.clientY,
      startValue:
        field === 'bodyLength'
          ? rocket.body.length
          : field === 'noseLength'
            ? rocket.noseCone.length
            : field === 'finWidth'
              ? rocket.fins.width
              : rocket.fins.length,
    };
    dragRef.current = start;
    setDragging(field);
  }

  function continueDrag(e: React.PointerEvent<SVGElement>) {
    const drag = dragRef.current;
    if (!drag) return;
    const svgEl = svgRef.current;
    if (!svgEl) return;
    const scaleY = VIEW_HEIGHT / svgEl.clientHeight;
    const scaleX = VIEW_WIDTH / svgEl.clientWidth;
    const dyView = (e.clientY - drag.startScreenY) * scaleY;
    const dxView = (e.clientX - drag.startScreenX) * scaleX;
    let delta = 0;
    if (drag.field === 'bodyLength' || drag.field === 'noseLength' || drag.field === 'finLength') {
      delta = -dyView / PIXELS_PER_CM;
    } else if (drag.field === 'finWidth') {
      delta = -dxView / PIXELS_PER_CM;
    }
    const limits = FIELD_LIMITS[drag.field];
    const next = Math.max(limits.min, Math.min(limits.max, drag.startValue + delta));
    if (drag.field === 'bodyLength') {
      updateRocket((r) => ({ ...r, body: { ...r.body, length: next } }));
    } else if (drag.field === 'noseLength') {
      updateRocket((r) => ({ ...r, noseCone: { ...r.noseCone, length: next } }));
    } else if (drag.field === 'finWidth') {
      updateRocket((r) => ({ ...r, fins: { ...r.fins, width: next } }));
    } else {
      updateRocket((r) => ({ ...r, fins: { ...r.fins, length: next } }));
    }
  }

  function endDrag() {
    dragRef.current = null;
    setDragging(null);
  }

  const bodyLengthPx = rocket.body.length * PIXELS_PER_CM;
  const bodyWidthPx = rocket.body.diameter * PIXELS_PER_CM;
  const noseConeLengthPx = rocket.noseCone.length * PIXELS_PER_CM;
  const finLengthPx = rocket.fins.length * PIXELS_PER_CM;
  const finWidthPx = rocket.fins.width * PIXELS_PER_CM;
  const finHeightPx = rocket.fins.height * PIXELS_PER_CM;

  const centerX = VIEW_WIDTH / 2;
  const bodyLeft = centerX - bodyWidthPx / 2;
  const bodyRight = centerX + bodyWidthPx / 2;
  const bodyTopY = BASELINE_Y - bodyLengthPx;
  const noseTipY = bodyTopY - noseConeLengthPx;

  const finTopY = BASELINE_Y - finHeightPx - finLengthPx;
  const finBottomY = BASELINE_Y - finHeightPx;

  const leftFin = `${bodyLeft},${finBottomY} ${bodyLeft - finWidthPx},${finBottomY} ${bodyLeft},${finTopY}`;
  const rightFin = `${bodyRight},${finBottomY} ${bodyRight + finWidthPx},${finBottomY} ${bodyRight},${finTopY}`;

  const cgY = BASELINE_Y - cg * PIXELS_PER_CM;
  const cpY = BASELINE_Y - cp * PIXELS_PER_CM;

  // Stage separator lines — drawn between stages, from below.
  const stageLines: number[] = [];
  let cumulative = 0;
  for (let i = 0; i < rocket.numStages - 1; i++) {
    const id = rocket.engineIds[i];
    if (!id) continue;
    const eng = getEngine(id);
    cumulative += eng.length / 10; // mm -> cm
    stageLines.push(cumulative);
  }

  return (
    <svg
      ref={svgRef}
      viewBox={`0 0 ${VIEW_WIDTH} ${VIEW_HEIGHT}`}
      className="w-full h-full max-h-[600px] group"
      role="img"
      aria-label="Rocket diagram"
    >
      {/* baseline */}
      <line
        x1={0}
        y1={BASELINE_Y}
        x2={VIEW_WIDTH}
        y2={BASELINE_Y}
        stroke="#0B3D91"
        strokeOpacity={0.2}
        strokeDasharray="4 3"
      />
      {/* body tube */}
      <rect
        x={bodyLeft}
        y={bodyTopY}
        width={bodyWidthPx}
        height={bodyLengthPx}
        fill="#4A90E2"
        stroke="#1A1A1A"
        strokeWidth={1}
      />
      {/* nose cone */}
      <path
        d={noseConePath(
          rocket.noseCone.shape ?? 'cone',
          bodyLeft,
          bodyRight,
          bodyTopY,
          noseTipY,
        )}
        fill="#D63333"
        stroke="#1A1A1A"
        strokeWidth={1}
      />
      {/* fins */}
      <polygon points={leftFin} fill="#1A1A1A" />
      <polygon points={rightFin} fill="#1A1A1A" />
      {/* center fin line (suggests the 3rd/4th fin behind) */}
      <line
        x1={centerX}
        y1={finBottomY}
        x2={centerX}
        y2={finTopY}
        stroke="#1A1A1A"
        strokeWidth={1}
      />

      {/* stage separator lines */}
      {stageLines.map((cmFromBase, i) => {
        const y = BASELINE_Y - cmFromBase * PIXELS_PER_CM;
        return (
          <line
            key={i}
            x1={bodyLeft - finWidthPx - 4}
            x2={bodyRight + finWidthPx + 4}
            y1={y}
            y2={y}
            stroke="#9400D3"
            strokeOpacity={0.6}
            strokeWidth={1.2}
            strokeDasharray="4 3"
          />
        );
      })}

      {/* CG marker — yin-yang style circle (alternating quadrants) */}
      <motion.g
        initial={false}
        animate={{ x: centerX, y: cgY }}
        transition={{ type: 'spring', stiffness: 240, damping: 26 }}
      >
        <circle r={9} fill="white" stroke="#0B1320" strokeWidth={1.2} />
        <path d="M 0,-9 A 9 9 0 0 1 0,9 L 0,0 Z" fill="#0B1320" />
        <path d="M 0,9 A 9 9 0 0 1 0,-9 L 0,0 Z" fill="white" />
        <path d="M -9,0 A 9 9 0 0 1 0,-9 L 0,0 Z" fill="#0B1320" />
        <path d="M 9,0 A 9 9 0 0 1 0,9 L 0,0 Z" fill="#0B1320" />
        <text
          x={14}
          y={4}
          fontSize={10}
          fontFamily="JetBrains Mono, ui-monospace, monospace"
          fill="#0B1320"
        >
          CG
        </text>
      </motion.g>

      {/* CP marker — small filled dot inside outlined circle */}
      <motion.g
        initial={false}
        animate={{ x: centerX, y: cpY }}
        transition={{ type: 'spring', stiffness: 240, damping: 26 }}
      >
        <circle r={9} fill="white" stroke="#0B3D91" strokeWidth={1.2} />
        <circle r={3} fill="#0B3D91" />
        <text
          x={14}
          y={4}
          fontSize={10}
          fontFamily="JetBrains Mono, ui-monospace, monospace"
          fill="#0B3D91"
        >
          CP
        </text>
      </motion.g>

      {/* Direct-manipulation drag handles — hidden until the diagram is
          hovered, except while a handle is actively being dragged. */}
      <DragHandle
        cx={centerX}
        cy={noseTipY}
        active={dragging === 'noseLength'}
        onPointerDown={(e) => startDrag('noseLength', e)}
        onPointerMove={continueDrag}
        onPointerUp={endDrag}
      />
      <DragHandle
        cx={bodyRight + 10}
        cy={(bodyTopY + (BASELINE_Y - finHeightPx - finLengthPx)) / 2}
        active={dragging === 'bodyLength'}
        onPointerDown={(e) => startDrag('bodyLength', e)}
        onPointerMove={continueDrag}
        onPointerUp={endDrag}
        orientation="vertical"
      />
      <DragHandle
        cx={bodyLeft - finWidthPx - 6}
        cy={finBottomY}
        active={dragging === 'finWidth'}
        onPointerDown={(e) => startDrag('finWidth', e)}
        onPointerMove={continueDrag}
        onPointerUp={endDrag}
        orientation="horizontal"
      />
      <DragHandle
        cx={bodyLeft - finWidthPx - 6}
        cy={finTopY}
        active={dragging === 'finLength'}
        onPointerDown={(e) => startDrag('finLength', e)}
        onPointerMove={continueDrag}
        onPointerUp={endDrag}
      />
    </svg>
  );
}

interface DragHandleProps {
  cx: number;
  cy: number;
  active: boolean;
  onPointerDown: (e: React.PointerEvent<SVGElement>) => void;
  onPointerMove: (e: React.PointerEvent<SVGElement>) => void;
  onPointerUp: (e: React.PointerEvent<SVGElement>) => void;
  orientation?: 'vertical' | 'horizontal';
}

function DragHandle({
  cx,
  cy,
  active,
  onPointerDown,
  onPointerMove,
  onPointerUp,
  orientation = 'vertical',
}: DragHandleProps) {
  return (
    <g
      className={
        active
          ? 'opacity-100 cursor-grabbing'
          : 'opacity-0 group-hover:opacity-100 transition-opacity ' +
            (orientation === 'horizontal' ? 'cursor-ew-resize' : 'cursor-ns-resize')
      }
      onPointerDown={onPointerDown}
      onPointerMove={onPointerMove}
      onPointerUp={onPointerUp}
      onPointerCancel={onPointerUp}
    >
      <circle cx={cx} cy={cy} r={9} fill="transparent" />
      <circle cx={cx} cy={cy} r={4} fill="#0B3D91" stroke="white" strokeWidth={1.5} />
    </g>
  );
}
