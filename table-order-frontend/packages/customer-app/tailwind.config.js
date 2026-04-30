/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        },
        status: {
          pending: '#f59e0b',
          preparing: '#3b82f6',
          completed: '#22c55e',
        },
      },
      minWidth: {
        touch: '44px',
      },
      minHeight: {
        touch: '44px',
      },
    },
  },
  plugins: [],
};
