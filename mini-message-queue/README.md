# Mini Message Queue

A lightweight message queue system built using **Java, Spring Boot, Spring Data JPA, MySQL, and Docker**.

The project demonstrates core message-queue concepts including message publishing, consumption, acknowledgment, retry handling, Dead Letter Queue (DLQ) processing, message replay, and concurrent consumer protection using pessimistic database locking.

---

##  Features

- Publish messages to named queues
- Consume pending messages
- Acknowledge successfully processed messages
- Message lifecycle management
- Automatic retry handling for unacknowledged messages
- Dead Letter Queue (DLQ) after maximum retries
- Replay messages from the DLQ
- Message status metrics
- Pessimistic database locking for concurrent consumers
- REST APIs
- MySQL persistence using Spring Data JPA
- Dockerized Spring Boot application
- Dockerized MySQL database
- Persistent MySQL storage using Docker volumes
- Separate Docker configuration using Spring profiles

---

##  Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs

### Database
- MySQL 8.0

### DevOps / Tools
- Docker
- Docker Compose
- Maven
- Git & GitHub
- IntelliJ IDEA
- Postman / REST Client

---

#  Architecture

The application follows a layered architecture.

```text
                    Client
              (Postman / REST Client)
                         |
                         | HTTP
                         v
              +----------------------+
              |   REST Controllers   |
              +----------+-----------+
                         |
                         v
              +----------------------+
              |    Service Layer     |
              |                      |
              | MessageService       |
              | DeadLetterService    |
              | MetricsService       |
              +----------+-----------+
                         |
                         v
              +----------------------+
              |   Repository Layer   |
              |    Spring Data JPA   |
              +----------+-----------+
                         |
                         v
              +----------------------+
              |      MySQL 8.0       |
              |                      |
              | messages             |
              | dead_letter_messages |
              +----------------------+
 Docker Architecture

The application and database run as separate Docker containers using Docker Compose.

                         Windows Host
                              |
                +-------------+-------------+
                |                           |
          localhost:8080              localhost:3307
                |                           |
                v                           v
      +-------------------+       +-------------------+
      | Spring Boot       |       | MySQL 8.0         |
      | Container         |       | Container         |
      | Port: 8080        |       | Port: 3306        |
      +---------+---------+       +-------------------+
                |
                | Docker Network
                |
                +----------------------+
                                       |
                                MySQL :3306
Port Mapping
Component	Container Port	Host Port
Spring Boot	8080	8080
MySQL	3306	3307

Inside Docker, Spring Boot connects to MySQL using:

mysql:3306

where mysql is the Docker Compose service name.

From the Windows host, MySQL is accessible through:

localhost:3307
 Project Structure
mini-message-queue/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-docker.properties
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
 Message Lifecycle

A message follows the following lifecycle:

                 Publish
                    |
                    v
               +---------+
               | PENDING |
               +----+----+
                    |
                 Consume
                    |
                    v
             +-------------+
             | PROCESSING  |
             +------+------+
                    |
                  ACK
                    |
                    v
              +---------+
              | SUCCESS |
              +---------+

If a message remains in PROCESSING without acknowledgment, the retry scheduler checks it after the configured timeout.

PROCESSING
     |
     | timeout
     v
  Retry
     |
     v
 PENDING
     |
     | consume again
     v
PROCESSING

After the maximum retry count is exceeded:

PROCESSING
     |
     | maximum retries exceeded
     v
   DEAD
     |
     v
Dead Letter Queue
 Retry Mechanism

A scheduled retry process periodically checks for messages that have remained in the PROCESSING state beyond the configured timeout.

The current configuration checks every 10 seconds and considers messages older than 30 seconds eligible for retry processing.

The message retry count is increased until the maximum retry limit is reached.

After the retry limit is exceeded, the message is moved to the Dead Letter Queue.

 Dead Letter Queue

Messages that cannot be successfully processed after the maximum number of retries are stored in the dead_letter_messages table.

The original message is marked as:

DEAD

The DLQ stores information such as:

Original message ID
Queue name
Message body
Original status
Retry count
Failure reason
Failure timestamp
 DLQ Replay

A message stored in the Dead Letter Queue can be replayed.

The replay process:

Dead Letter Message
        |
        v
Create new Message
        |
        v
     PENDING
        |
        v
Available for consumption

When replayed:

A new message is created
Its status is set to PENDING
Its retry count is reset
The corresponding DLQ entry is removed
 Concurrency Handling

The consumer operation uses pessimistic database locking.

The repository uses:

@Lock(LockModeType.PESSIMISTIC_WRITE)

when selecting the next pending message.

This helps prevent two consumers from simultaneously claiming the same message.

Conceptually:

Consumer A ----\
                \
                 ---> Database Lock ---> Message
                /
Consumer B ----/

Only one consumer can successfully claim the locked message.

A concurrency test was performed by sending two simultaneous consume requests for the same queue. One request successfully claimed the message while the other did not receive a pending message.

 Database Design
messages

The main message table contains information related to queued messages.

Important fields include:

Field	Purpose
id	Message identifier
queue_name	Queue to which the message belongs
message_body	Message content
status	Current message status
retry_count	Number of retry attempts
locked_at	Time when the message was locked
locked_by	Consumer information
created_at	Creation timestamp
updated_at	Last update timestamp
dead_letter_messages

Stores messages that exceeded the retry limit.

Field	Purpose
id	DLQ record identifier
original_message_id	Original message identifier
queue_name	Original queue
message_body	Message content
original_status	Status before moving to DLQ
retry_count	Retry count
reason	Reason for failure
failed_at	Failure timestamp
 API Documentation

The application exposes the following REST APIs.

1. Publish Message
Endpoint
POST /messages/publish
Request
{
  "queueName": "orders",
  "messageBody": "Order #101 created"
}
Example PowerShell Request
Invoke-RestMethod `
  -Uri "http://localhost:8080/messages/publish" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"queueName":"orders","messageBody":"Order #101 created"}'
Result

The newly created message starts with:

PENDING
2. Consume Message
Endpoint
GET /messages/consume/{queueName}
Example
GET /messages/consume/orders

A successfully consumed message changes from:

PENDING → PROCESSING
3. Acknowledge Message
Endpoint
POST /messages/ack/{id}
Example
POST /messages/ack/1

A successfully acknowledged message changes from:

PROCESSING → SUCCESS

Only messages currently in the PROCESSING state can be acknowledged.

4. Replay Dead Letter Message
Endpoint
POST /dead-letters/{id}/replay
Example
POST /dead-letters/1/replay

The DLQ message is converted into a new pending message.

DLQ
 |
 | replay
 v
PENDING
5. Get Metrics
Endpoint
GET /metrics

Returns message counts by status.

Example:

{
  "pending": 3,
  "processing": 0,
  "success": 5,
  "dead": 1
}
 Configuration

The application uses separate configuration files for local and Docker execution.

Local
application.properties

The local configuration connects to MySQL running on the host machine.

Docker
application-docker.properties

The Docker configuration connects to the MySQL Compose service:

jdbc:mysql://mysql:3306/message_queue
Environment Variables

Database credentials are provided through environment variables.

Example .env:

DB_PASSWORD=your_password

The actual .env file is intentionally excluded from Git using:

.env

The application configuration uses:

spring.datasource.password=${DB_PASSWORD}

The real database password should never be committed to the repository.

 Running with Docker
Prerequisites

Install:

Java 21
Docker Desktop
Git

Maven does not need to be installed separately because the project includes Maven Wrapper.

1. Clone the repository
git clone https://github.com/Kavinayaboopathi/mini-message-queue.git
cd mini-message-queue
2. Create .env

Create a .env file in the project directory:

DB_PASSWORD=your_password
3. Build the application

On Windows:

.\mvnw.cmd clean package -DskipTests
4. Build the Docker image
docker build -t mini-message-queue:latest .
5. Start the containers
docker compose up -d
6. Check container status
docker compose ps

Expected services:

mini-message-queue-app
mini-message-queue-mysql
7. Check application logs
docker logs mini-message-queue-app

The application should contain:

Started MiniMessageQueueApplication
8. Stop the application
docker compose down

The MySQL data is stored in the Docker volume:

mysql_data

Avoid using:

docker compose down -v

unless you intentionally want to remove the Docker volume and its stored database data.

 Testing

The application has been manually tested for:

Message publishing
PUBLISH → PENDING
Message consumption
PENDING → PROCESSING
Acknowledgment
PROCESSING → SUCCESS
Retry handling
PROCESSING
     ↓
timeout
     ↓
PENDING
Dead Letter Queue
PROCESSING
     ↓
maximum retries
     ↓
DEAD
     ↓
DLQ
DLQ Replay
DLQ
 ↓
new message
 ↓
PENDING
Concurrent consumption

Two consumers attempting to consume the same message were tested using simultaneous requests. Pessimistic locking ensures that the same message is not simultaneously claimed by multiple consumers.

 Complete Message Flow
                 ┌──────────────┐
                 │    Publish   │
                 └──────┬───────┘
                        ↓
                  ┌───────────┐
                  │  PENDING  │
                  └─────┬─────┘
                        │
                     Consume
                        ↓
                ┌──────────────┐
                │  PROCESSING  │
                └───────┬──────┘
                        │
                 ┌──────┴──────┐
                 │             │
                ACK          Timeout
                 │             │
                 ↓             ↓
            ┌─────────┐     Retry
            │ SUCCESS │       │
            └─────────┘       ↓
                           PENDING
                              │
                         Retry limit
                              │
                              ↓
                           ┌──────┐
                           │ DEAD │
                           └──┬───┘
                              ↓
                             DLQ
                              │
                           Replay
                              │
                              ↓
                           PENDING
 Project Highlights
REST-based message queue implementation
Persistent message storage using MySQL
Transactional message processing
Pessimistic locking for concurrent consumers
Automatic retry handling
Dead Letter Queue support
DLQ replay mechanism
Scheduled background processing
Dockerized application and database
Persistent Docker database volume
Environment-based database credentials
 Future Improvements

Potential improvements include:

Swagger/OpenAPI documentation
Spring Boot Actuator health monitoring
Docker health checks
Global exception handling
Additional automated integration tests
Improved metrics and monitoring
GitHub Actions CI/CD
Authentication and authorization
Multiple consumer workers
Improved distributed scheduling/locking

 Author

Kavinaya B S


BE Computer Science and Engineering

 License

This project is intended for learning and demonstration purposes.