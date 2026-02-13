import { Outlet } from "react-router-dom";
import { WorkLogListPage } from "./WorkLogListPage";
import "./WorkLogLayout.css";

/**
 * WorkLogLayout component.
 *
 * This component acts as a layout wrapper for WorkLog-related routes.
 *
 * It combines:
 * - Left side: WorkLog list (navigation / overview)
 * - Right side: Nested route content (summary, edit, etc.)
 *
 * The <Outlet /> component renders child routes
 * defined inside this layout in React Router configuration.
 */
const WorkLogLayout = () => {
  return (
    <>
     {/* Main layout container.
          CSS class "worklogs-layout" likely defines grid or flex structure. */}
      <div className="worklogs-layout">

        {/* Left section: always visible WorkLog list */}
        <aside
         className="worklogs-sidebar">
          <WorkLogListPage />
        </aside>

         {/* Right section: dynamic content based on current nested route.
            Example:
            /worklogs/1/summary
            /worklogs/1/edit
        */}
        <main className="worklogs-content">
        <Outlet />
        </main>
      </div>
    </>
  );
};
export default WorkLogLayout;
