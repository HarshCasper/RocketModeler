import type { Material, MaterialId } from './types';

// Densities in g/cm^3, mirroring the original applet's Constants.
export const MATERIALS: Record<MaterialId, Material> = {
  balsa: { id: 'balsa', label: 'Balsa', density: 0.1281 },
  plastic: { id: 'plastic', label: 'Plastic', density: 1.049 },
  'hollow-plastic': { id: 'hollow-plastic', label: 'Hollow plastic', density: 0.1121 },
  custom: { id: 'custom', label: 'Custom', density: 0.5 },
};

export const NOSE_CONE_MATERIALS: MaterialId[] = ['balsa', 'hollow-plastic', 'custom'];

export const FIN_MATERIALS: MaterialId[] = ['balsa', 'plastic', 'custom'];

export const BODY_TUBE_DENSITY = 0.516; // g/cm^3, original Constants.bodytubedensity
export const BODY_TUBE_THICKNESS = 0.1; // cm, original Constants.bodytubethickness

export const CM_PER_INCH = 2.54;

export function noseConeDensity(materialId: MaterialId, custom?: number): number {
  if (materialId === 'custom') return custom ?? MATERIALS.custom.density;
  return MATERIALS[materialId].density;
}

export function finDensityThickness(
  materialId: MaterialId,
  thicknessInches: 0.125 | 0.25,
  custom?: number,
): number {
  const density = materialId === 'custom' ? custom ?? MATERIALS.custom.density : MATERIALS[materialId].density;
  return density * (thicknessInches * CM_PER_INCH);
}
