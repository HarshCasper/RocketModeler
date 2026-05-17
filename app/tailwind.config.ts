import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        paper: '#F4F7FB',
        ink: '#0B1320',
        nasa: {
          DEFAULT: '#0B3D91',
          light: '#1E5BB7',
          dark: '#062866',
        },
        rocket: {
          tube: '#4A90E2',
          nose: '#D63333',
          fin: '#1A1A1A',
        },
        stability: {
          good: '#2E8B57',
          warn: '#E0A116',
          bad: '#C0392B',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          'system-ui',
          '-apple-system',
          'Segoe UI',
          'Helvetica',
          'Arial',
          'sans-serif',
        ],
        mono: ['JetBrains Mono', 'ui-monospace', 'SFMono-Regular', 'Menlo', 'monospace'],
      },
      backgroundImage: {
        blueprint:
          'linear-gradient(rgba(11,61,145,0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(11,61,145,0.04) 1px, transparent 1px)',
      },
      backgroundSize: {
        grid: '24px 24px',
      },
    },
  },
  plugins: [],
} satisfies Config;
