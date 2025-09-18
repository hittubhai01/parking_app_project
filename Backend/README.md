# Smart Parking System - Backend    

This project is the complete backend for a smart parking system, including a REST API, PostgreSQL database, and Nginx reverse proxy, all containerized with Docker.

## Features

- **User Authentication:** Secure user registration and login using JWT.
- **Hierarchical Parking Structure:** Full CRUD API for managing Parking Lots, Floors, Rows, and Slots.
- **Real-time Slot Updates:** Dedicated endpoint for IoT devices (e.g., Raspberry Pi) to push slot status changes.
- **API Documentation:** Interactive Swagger UI for exploring and testing the API.
- **Containerized:** Fully containerized with Docker and Docker Compose for easy setup and deployment.
- **Automated Testing:** Includes a suite of `pytest` unit/integration tests and an end-to-end system test script.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## How to Run the Application

Follow these steps to get the application running on your local machine.

### 1. Set Up Environment Variables

The application uses a `.env` file to manage environment variables. An example file (`.env.example`) is provided.

First, make a copy of the example file:

```sh
# For Windows (Command Prompt)
copy .env.example .env

# For Windows (PowerShell)
cp .env.example .env

# For Linux/macOS
cp .env.example .env
```

The default values in the `.env` file are pre-configured to work with the `docker-compose.yml` setup, so you don't need to change anything unless you want to customize the database credentials or secret keys.

### 2. Build and Start the Containers
    
#### Make sure the commands are compatible with your OS(recommended to use powershell or bash for windows)

This command will build the Docker images for the Flask application and Nginx, and start all the services (app, database, proxy) in the background.

```sh
docker-compose up --build -d
```

# 3. Initialize the Database

The first time you set up this project you need to ensure the database schema is created from the SQLAlchemy models and recorded in Alembic migration history.

**Important:** if the repository already contains a `migrations/` folder (or `app/migrations/`) then **do not run `flask db init`** again — use the existing migrations and `stamp` the DB if needed (see below). We recommend `app/migrations` as the canonical migrations folder and configuring Flask-Migrate accordingly (example shown at the end).

---

## If you are starting from scratch (no migrations present)

Run these commands **inside the running `app` container** (preferred format shown):

```sh
# start containers first
docker-compose up -d

# create alembic env (only run once when no migrations folder exists)
docker-compose exec app flask db init

# generate the initial migration from your models
docker-compose exec app flask db migrate -m "Initial migration"

# apply migrations to the database
docker-compose exec app flask db upgrade
```

### If your container environment does not automatically expose the Flask app, use:

```sh
docker-compose exec -e FLASK_APP=wsgi:app app flask db ...
```

### but prefer `docker-compose exec app flask db ...` if `FLASK_APP` is already configured in the container.

---

## If a migrations folder already exists (common for this repo)

**Do not** re-run `flask db init`. Instead:

1. Confirm the DB’s current revision:

```sh
docker-compose exec app flask db current
```

2. If the database already contains the tables (for example you loaded them from SQL), mark the DB as up-to-date with the existing migration history (this **creates the `alembic_version` row** and prevents Alembic from trying to recreate existing tables):

```sh
docker-compose exec app flask db stamp head
```

3. To generate a migration for later model changes:

```sh
# generate change script (autogenerate)
docker-compose exec app flask db migrate -m "Describe change"

# apply it
docker-compose exec app flask db upgrade
```

---

## Important Alembic tips & commands

* Show the migration history:

```sh
docker-compose exec app flask db history --verbose
```

* Show heads (multiple heads = multiple independent revision chains):

```sh
docker-compose exec app flask db heads
```


---


## 🗄️ Populating the Database with Initial Data

After migrations have created the tables (via flask db upgrade or by stamping to head + applying delta migrations), populate your database with initial data using your SQL script. Run the following commands:

```sh
# Copy the SQL file into the running database container
docker cp populate_parking_data.sql backend-db-1:/populate_parking_data.sql

# Execute the SQL script inside the database container
docker exec -it backend-db-1 psql -U parking_user -d parking_db -f /populate_parking_data.sql
```

Your application is now running!

## Accessing the Application

Once the services are running, you can access the application in your web browser.

- **Main Landing Page:**
  - **URL:** `http://localhost`
  - **Description:** Visit this URL to see a welcome page confirming that the application is running correctly.

- **API Documentation (Swagger UI):**
  - **URL:** `http://localhost/apidocs`
  - **Description:** This is an interactive page where you can see all available API endpoints, their required parameters, and test them directly from your browser. This is the primary tool for API testing.

## Running the Tests

The project includes two types of tests.

### Pytest (Unit/Integration Tests)

These tests check the application's internal logic using an in-memory database. They are fast and ideal for running during development.

```sh
docker-compose exec app pytest
```

### End-to-End Test (e2e_test.py)

This script tests the entire live system running in Docker, including the Nginx proxy and PostgreSQL database. It's a great way to verify that all the pieces are working together correctly.

**Run the script as a standalone Python script inside the app container:**

```sh
docker-compose exec app python e2e_test.py
```

- The script will register a user, log in, create a parking lot, floor, row, and slot, update slot status, verify the update, and check parking lot stats.
- **Important:** The payloads sent in this script (and in your API requests) must match the fields defined in your SQLAlchemy models. Do not include fields like `zip_code`, `state`, `country`, or `phone_number` unless they exist in the model.
- If you see errors like `Unknown field`, check that your request data matches the model and Marshmallow schema exactly.

#### Example Parking Lot Creation Payload

```json
{
  "name": "Test Lot",
  "address": "123 Test St",
  "city": "Test City",
  "landmark": "Near Test Landmark",
  "latitude": 12.345,
  "longitude": 67.890,
  "physical_appearance": "Multi-storey",
  "parking_ownership": "Private",
  "parking_surface": "Concrete",
  "has_cctv": "Yes",
  "has_boom_barrier": "Yes",
  "ticket_generated": "Yes",
  "entry_exit_gates": "2",
  "weekly_off": "Sunday",
  "parking_timing": "8am-10pm",
  "vehicle_types": "Car,Bike",
  "car_capacity": 50,
  "available_car_slots": 50,
  "two_wheeler_capacity": 20,
  "available_two_wheeler_slots": 20,
  "parking_type": "Open",
  "payment_modes": "Cash,Card",
  "car_parking_charge": "20/hr",
  "two_wheeler_parking_charge": "10/hr",
  "allows_prepaid_passes": "No",
  "provides_valet_services": "No",
  "value_added_services": "EV Charging"
}
```

## Troubleshooting

- **Schema Mismatch Errors:**
  - If you get errors like `Unknown field`, ensure your request data only includes fields present in the model and schema.
  - Align your Marshmallow schema and test data with your final `models.py`.

- **Test Failures:**
  - If unit or e2e tests fail, check the error output for details about missing or extra fields, or database issues.

## Stopping the Application

To stop all the running containers, use:

```sh
docker-compose down
``` 
=======

## 📚 API Endpoints & Structure Reference
For a complete list of all endpoints and their request/response formats, see [API Specs (REST_API_Specs)](../REST_API_Specs/ADMIN_APP_REST_API_SPECS.md).
