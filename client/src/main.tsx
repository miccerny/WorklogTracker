/**
 * Application entry point.
 *
 * This file bootstraps the React application and mounts it
 * into the root DOM element defined in index.html.
 *
 * Responsibilities:
 * - Create React root (React 18+ API)
 * - Enable development checks via StrictMode
 * - Configure routing using BrowserRouter
 * - Render the main App component
 */
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { BrowserRouter } from "react-router-dom";
import { FlashProvider } from "./context/flash.tsx";

// Get the root DOM element from index.html
// The non-null assertion (!) tells TypeScript that we are sure
// the element exists (because Vite template guarantees it).
const rootElement = document.getElementById("root")!;

// Create React root (React 18 concurrent rendering API)
const root = createRoot(rootElement);

// Render the application tree
root.render(
  <StrictMode>
    {/* 
      StrictMode enables additional checks and warnings
      in development mode (e.g., detecting unsafe lifecycle usage).
      It does NOT affect production build behavior.
    */}

    <BrowserRouter>
      <FlashProvider>
        {/*
        BrowserRouter enables client-side routing using
        the HTML5 history API. All routes inside App
        can now use react-router features.
      */}
        <App />
      </FlashProvider>
    </BrowserRouter>
  </StrictMode>,
);
