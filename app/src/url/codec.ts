import { deflate, inflate } from 'pako';
import type { Rocket } from '../domain/types';

const PREFIX = '#r=';

function toBase64Url(bytes: Uint8Array): string {
  let bin = '';
  for (let i = 0; i < bytes.length; i++) bin += String.fromCharCode(bytes[i]);
  const b64 = typeof btoa === 'function' ? btoa(bin) : Buffer.from(bin, 'binary').toString('base64');
  return b64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function fromBase64Url(s: string): Uint8Array {
  const b64 = s.replace(/-/g, '+').replace(/_/g, '/') + '='.repeat((4 - (s.length % 4)) % 4);
  const bin = typeof atob === 'function' ? atob(b64) : Buffer.from(b64, 'base64').toString('binary');
  const bytes = new Uint8Array(bin.length);
  for (let i = 0; i < bin.length; i++) bytes[i] = bin.charCodeAt(i);
  return bytes;
}

export function encodeRocket(r: Rocket): string {
  const json = JSON.stringify(r);
  const compressed = deflate(json);
  return PREFIX + toBase64Url(compressed);
}

export function decodeRocket(hash: string): Rocket | null {
  if (!hash.startsWith(PREFIX)) return null;
  try {
    const bytes = fromBase64Url(hash.slice(PREFIX.length));
    const json = inflate(bytes, { to: 'string' });
    const parsed = JSON.parse(json) as Rocket;
    if (parsed.schemaVersion === 1) return parsed;
    return null;
  } catch {
    return null;
  }
}
