import React, { useState, useEffect } from 'react';
import './ThemeToggle.css';

const ThemeToggle = () => {
  const [isDark, setIsDark] = useState(false);

  // Initialize theme on component mount
  useEffect(() => {
      const savedTheme = localStorage.getItem('theme');
      
      if (savedTheme) {
      setIsDark(savedTheme === 'dark');
        document.documentElement.setAttribute('data-theme', savedTheme);
      } else {
        const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      setIsDark(systemPrefersDark);
      document.documentElement.setAttribute('data-theme', systemPrefersDark ? 'dark' : 'light');
      }

    // Listen for system theme changes
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleSystemThemeChange = (e) => {
      if (!localStorage.getItem('theme')) {
        setIsDark(e.matches);
        document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light');
      }
    };

    mediaQuery.addEventListener('change', handleSystemThemeChange);
    
    return () => {
      mediaQuery.removeEventListener('change', handleSystemThemeChange);
    };
  }, []);

  const handleToggle = () => {
    const newTheme = !isDark;
    setIsDark(newTheme);
    
    // Apply theme to document
    document.documentElement.setAttribute('data-theme', newTheme ? 'dark' : 'light');
    
    // Save preference
    localStorage.setItem('theme', newTheme ? 'dark' : 'light');
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleToggle();
    }
  };

  return (
    <div className="theme-toggle-wrapper">
      <label className="theme-switch">
        <input 
          type="checkbox" 
          className="theme-switch__checkbox"
          checked={isDark}
          onChange={handleToggle}
      onKeyDown={handleKeyPress}
          aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
        />
        <div className="theme-switch__container">
          <div className="theme-switch__clouds"></div>
          <div className="theme-switch__stars-container">
            <svg viewBox="0 0 44 20" fill="currentColor">
              <path d="M2,2 L3,2 L2.5,3 Z"/>
              <path d="M8,4 L9,4 L8.5,5 Z"/>
              <path d="M14,2 L15,2 L14.5,3 Z"/>
              <path d="M20,5 L21,5 L20.5,6 Z"/>
              <path d="M26,3 L27,3 L26.5,4 Z"/>
              <path d="M32,6 L33,6 L32.5,7 Z"/>
              <path d="M38,4 L39,4 L38.5,5 Z"/>
              <path d="M44,2 L45,2 L44.5,3 Z"/>
              <path d="M5,8 L6,8 L5.5,9 Z"/>
              <path d="M11,10 L12,10 L11.5,11 Z"/>
              <path d="M17,8 L18,8 L17.5,9 Z"/>
              <path d="M23,11 L24,11 L23.5,12 Z"/>
              <path d="M29,9 L30,9 L29.5,10 Z"/>
              <path d="M35,12 L36,12 L35.5,13 Z"/>
              <path d="M41,10 L42,10 L41.5,11 Z"/>
              <path d="M2,14 L3,14 L2.5,15 Z"/>
              <path d="M8,16 L9,16 L8.5,17 Z"/>
              <path d="M14,14 L15,14 L14.5,15 Z"/>
              <path d="M20,17 L21,17 L20.5,18 Z"/>
              <path d="M26,15 L27,15 L26.5,16 Z"/>
              <path d="M32,18 L33,18 L32.5,19 Z"/>
              <path d="M38,16 L39,16 L38.5,17 Z"/>
              <path d="M44,14 L45,14 L44.5,15 Z"/>
            </svg>
          </div>
          <div className="theme-switch__circle-container">
            <div className="theme-switch__sun-moon-container">
              <div className="theme-switch__moon">
                <div className="theme-switch__spot"></div>
                <div className="theme-switch__spot"></div>
                <div className="theme-switch__spot"></div>
              </div>
        </div>
        </div>
        </div>
      </label>
      </div>
  );
};

export default ThemeToggle;