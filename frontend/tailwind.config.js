/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'dungeon-dark': '#1a1a2e',
        'dungeon-purple': '#16213e',
        'dungeon-blue': '#0f3460',
        'dungeon-accent': '#e94560',
        'dungeon-gold': '#ffd700',
      },
      animation: {
        'dice-roll': 'dice-roll 0.5s ease-out',
        'pulse-glow': 'pulse-glow 2s ease-in-out infinite',
      },
      keyframes: {
        'dice-roll': {
          '0%': { transform: 'rotate(0deg) scale(1)' },
          '50%': { transform: 'rotate(180deg) scale(1.2)' },
          '100%': { transform: 'rotate(360deg) scale(1)' },
        },
        'pulse-glow': {
          '0%, 100%': { boxShadow: '0 0 10px rgba(233, 69, 96, 0.5)' },
          '50%': { boxShadow: '0 0 20px rgba(233, 69, 96, 0.8)' },
        },
      },
    },
  },
  plugins: [],
}
