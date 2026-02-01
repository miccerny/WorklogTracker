# Time Tracker / Worklog Application

A full-stack web application for tracking time spent on projects.
The application is designed design-first, using UML diagrams as a blueprint before implementation.

## Project Goal

The goal of this project is to build a realistic time-tracking system that:

- allows users to track work time using a timer (start / stop)
- stores time entries only after explicit Save confirmation
- supports project-based reporting
- demonstrates work with date & time, calculations, validations, and reporting
- shows a clean backend–frontend contract using DTOs

This project is intended as a portfolio project.

## Design-First Approach (UML)

Before writing any production code, the application was designed using UML diagrams.
The UML design defines the final scope of the project and serves as a single source of truth during implementation.

### Included UML diagrams

- Use Case Diagram – user actions and application features
- Class Diagram – domain entities and relationships
- Sequence Diagram – timer → save → summary flow
- Component Diagram – frontend / backend / database overview

[!/docs/uml]

## Timer & WorkLog Flow

1. User opens the Timer page for a selected project
2. Clicks Start → timer starts running (frontend only)
3. Clicks Stop → a draft time entry is created on the client
4. Clicks Save → the time entry is stored as a WorkLog
5. User is redirected to the Project Summary view
6. This approach prevents accidental data creation and keeps the database clean.

##  Domain Model (Simplified)
### User
- owns Projects
- owns WorkLogs

### Project
- groups WorkLogs
- can be activated / deactivated

### WorkLog
- start and end timestamps (LocalDateTime)
- duration calculated in seconds
- optional note
- validated against overlapping entries

## REST API Overview
### Worklogs
- GET      /api/worklog
- POST     /api/worklog
- PUT      /api/projects/{id}

### Timers
- POST   /api/timer
- GET    /api/timer?from=&to=&projectId=
- PUT    /api/timer/{id}
- DELETE /api/timer/{id}

### Reports
- GET /api/reports/summary?projectId=&from=&to=

## Tech Stack
### Backend
- Java 17
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate
- PostgreSQL 
- MapStruct
### Frontend
- React
- TypeScript
- React Router
- Client-side timer logic

## Validation Rules
- endAt > startAt
- break duration must be smaller than total duration
- no overlapping WorkLog entries for the same user and day
- all time calculations are done in seconds
