import './index.css';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { SITE_NAME } from './Config';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import React, { Suspense, lazy } from 'react';
import Snowfall from './components/snowfall';
import ThemeToggle from './components/ThemeToggle'; // ✅ Imported

const HomePage = lazy(() => import('./pages/HomePage'));
const AboutPage = lazy(() => import('./pages/AboutPage'));
const ContactPage = lazy(() => import('./pages/ContactPage'));
const FeaturesPage = lazy(() => import('./pages/FeaturesPage'));

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <Helmet>
          <title>{SITE_NAME} | Elevate Collaboration</title>
        </Helmet>

        {/* Snowfall visible on all pages */}
        <Snowfall />

        {/* Theme toggle always visible in top-right corner */}
        <ThemeToggle /> {/* ✅ ADDED HERE */}

        <Navbar role="navigation" aria-label="Main Navigation" />

        <main className="main-content" role="main" tabIndex={-1}>
          <Suspense fallback={<div className="loading">Loading...</div>}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/contact" element={<ContactPage />} />
              <Route path="/features" element={<FeaturesPage />} />
              <Route
                path="*"
                element={
                  <div className="not-found">
                    <h1>404 - Page Not Found</h1>
                    <p>The page you're looking for doesn't exist.</p>
                  </div>
                }
              />
            </Routes>
          </Suspense>
        </main>

        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;
