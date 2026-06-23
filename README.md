# Helpdesk Ticketing System

## Project Overview

The Helpdesk Ticketing System is a Spring Boot REST API designed to manage support tickets within an organization. The system allows users to create tickets, assign agents, track ticket status changes, manage comments, enforce SLA rules, and generate operational reports.

The project uses PostgreSQL as the database and Spring Data JPA for data persistence.

---

# Technology Stack

* Java 17
* Spring Boot 3
* Spring Data JPA
* PostgreSQL 16
* Maven
* Lombok
* Docker
* DBVisualizer
* Postman
---
## How to Run

1. Start PostgreSQL using Docker:

```bash
docker compose up -d
```

2. Open the project in IntelliJ IDEA.

3. Configure `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk_db
spring.datasource.username=helpdesk_user
spring.datasource.password=29999login
```

4. Run the Spring Boot application.

5. Access the APIs using Postman:

```http
http://localhost:8080
```

---

# Database Entities

## USERS

Stores end-user information.

| Column     | Type      |
| ---------- | --------- |
| user_id    | BIGSERIAL |
| fname      | VARCHAR   |
| lname      | VARCHAR   |
| email      | VARCHAR   |
| department | VARCHAR   |
| created_at | TIMESTAMP |

---

## AGENTS

Stores helpdesk support agents.

| Column         | Type      |
| -------------- | --------- |
| agent_id       | BIGSERIAL |
| fname          | VARCHAR   |
| lname          | VARCHAR   |
| email          | VARCHAR   |
| specialization | VARCHAR   |
| created_at     | TIMESTAMP |

---

## SLA_RULE

Stores SLA definitions.

| Column       | Type      |
| ------------ | --------- |
| slarule_id   | BIGSERIAL |
| priority     | VARCHAR   |
| target_hours | INTEGER   |

---

## TICKETS

Stores support tickets.

| Column            | Type      |
| ----------------- | --------- |
| ticket_id         | BIGSERIAL |
| title             | VARCHAR   |
| description       | TEXT      |
| priority          | VARCHAR   |
| category          | VARCHAR   |
| status            | VARCHAR   |
| created_at        | TIMESTAMP |
| resolved_at       | TIMESTAMP |
| closed_at         | TIMESTAMP |
| raised_by_user_id | BIGINT    |
| assigned_agent_id | BIGINT    |
| slarule_id        | BIGINT    |

---

## COMMENTS

Stores ticket comments.

| Column      | Type      |
| ----------- | --------- |
| comment_id  | BIGSERIAL |
| body        | VARCHAR   |
| author_type | VARCHAR   |
| created_at  | TIMESTAMP |
| ticket_id   | BIGINT    |

---

## TICKET_STATUS_LOG

Stores ticket status history.

| Column      | Type      |
| ----------- | --------- |
| log_id      | BIGSERIAL |
| from_status | VARCHAR   |
| to_status   | VARCHAR   |
| changed_at  | TIMESTAMP |
| ticket_id   | BIGINT    |

---

# Business Rules

* Every ticket belongs to one user.
* A ticket may be assigned to one agent.
* A ticket must be linked to one SLA rule.
* A ticket may contain multiple comments.
* Every status change is recorded in Ticket Status Log.
* Invalid status transitions are blocked.
* Closed tickets cannot be assigned to agents.
* SLA rules define maximum resolution time targets.

---
## Status Transition Rules

The ticket status is implemented as a state machine in the service layer.

Valid transitions:

| From | To |
|--------|--------|
| OPEN | IN_PROGRESS |
| IN_PROGRESS | RESOLVED |
| RESOLVED | CLOSED |
| RESOLVED | REOPENED |
| REOPENED | IN_PROGRESS |
| REOPENED | RESOLVED |

All invalid transitions are rejected with HTTP 409 Conflict.

Examples of invalid transitions:

- OPEN → CLOSED
- CLOSED → OPEN
- CLOSED → REOPENED
- IN_PROGRESS → CLOSED
---

# Ticket Status Workflow

```text
OPEN
   ↓
IN_PROGRESS
   ↓
RESOLVED
   ↓
CLOSED
```

Reopen Flow:

```text
RESOLVED
   ↓
REOPENED
   ↓
IN_PROGRESS
```

---

# API Endpoints

## User APIs

Create User

```http
POST /api/users
```

Get All Users

```http
GET /api/users
```

---

## Agent APIs

Create Agent

```http
POST /api/agents
```

Get All Agents

```http
GET /api/agents
```

---

## Ticket APIs

Create Ticket

```http
POST /api/tickets?userId={id}&slaRuleId={id}
```

Get All Tickets

```http
GET /api/tickets
```

Get Ticket By ID

```http
GET /api/tickets/{ticketId}
```

Assign Agent

```http
POST /api/tickets/{ticketId}/assign?agentId={id}
```

Update Ticket Status

```http
PUT /api/tickets/{ticketId}/status?status=IN_PROGRESS
```

---

## Comment APIs

Add Comment

```http
POST /api/comments/ticket/{ticketId}
```

Get All Comments

```http
GET /api/comments
```

Get Comments By Ticket

```http
GET /api/comments/ticket/{ticketId}
```

---

# Reporting APIs

Get Overdue Tickets

```http
GET /api/tickets/overdue
```

Get Average Resolution Time

```http
GET /api/tickets/metrics/avg-resolution-time
```

---

# Dashboard APIs

Get Total Tickets

```http
GET /api/tickets/dashboard/total
```

Get Open Tickets

```http
GET /api/tickets/dashboard/open
```

Get Closed Tickets

```http
GET /api/tickets/dashboard/closed
```

---

# Filtering APIs

Filter By Status

```http
GET /api/tickets?status=OPEN
```

Filter By Priority

```http
GET /api/tickets?priority=HIGH
```

Filter By Category

```http
GET /api/tickets?category=Hardware
```

Filter By Assigned Agent

```http
GET /api/tickets?assignedTo=1
```

---

# Exception Handling

The system uses Global Exception Handling.

Example:

```json
{
  "timestamp": "2026-06-23T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Invalid status transition from CLOSED to OPEN"
}
```

---
## Assumptions

- Every ticket must be linked to a user.
- Every ticket must be linked to one SLA rule.
- A ticket starts with OPEN status by default.
- A CLOSED ticket is considered final and cannot be reopened.
- An agent cannot be assigned to a CLOSED ticket.
- Resolution time is calculated using created_at and resolved_at timestamps.
- Overdue tickets are tickets that exceed their SLA target time and are not yet closed.
---
# Project Features

* User Management
* Agent Management
* Ticket Management
* Comment Management
* SLA Tracking
* Status Change Logging
* State Machine Validation
* Dashboard Metrics
* Ticket Filtering
* Global Exception Handling
* PostgreSQL Integration
* Spring Data JPA Persistence
