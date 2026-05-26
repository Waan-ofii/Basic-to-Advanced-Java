# Requirements Document

## Introduction

This document defines the requirements for the Distributed University Student Learning Material Sharing System — a Java-based client-server application that enables university students to authenticate, browse, upload, download, and receive notifications about academic learning materials organized by department and academic year. The system uses JavaFX for the client UI, TCP sockets for file transfer, RMI for remote service invocation, UDP for broadcast notifications, and JDBC with MySQL for persistence.

## Glossary

- **System**: The complete distributed application (client + server).
- **Client**: The JavaFX desktop application running on a student's machine.
- **Server**: The Java server application hosting TCP, RMI, and UDP services.
- **AuthService**: The RMI-exposed service responsible for student authentication and session management.
- **MaterialService**: The RMI-exposed service responsible for material search, retrieval, upload, and download.
- **TCPServer**: The server component that accepts TCP socket connections and dispatches them to handler threads.
- **ClientHandler**: A per-connection server thread that processes one client's TCP requests.
- **UDPBroadcaster**: The server component that sends UDP datagram announcements to all clients.
- **UDPListener**: The client-side background thread that receives UDP broadcast announcements.
- **BusinessLogic**: The shared server-side layer that coordinates DAO, file storage, and broadcast operations.
- **DAO**: Data Access Object — a class that executes parameterized SQL queries via JDBC.
- **SessionStore**: A server-side `ConcurrentHashMap` mapping session tokens to authenticated Student objects.
- **SessionToken**: A UUID v4 string issued on successful login and used to authorize subsequent requests.
- **Material**: A learning resource record containing metadata and a server-side file path.
- **MaterialMetadata**: A serializable transfer object carrying title, course, department, year, filename, and uploader ID.
- **AuthResult**: A serializable transfer object returned by login, carrying success flag, session token, error message, and student data.
- **UploadResult**: A serializable transfer object returned by upload, carrying success flag and material ID or error message.
- **StorageRoot**: The server-side base directory under which uploaded files are organized as `{storageRoot}/{department}/{year}/{filename}`.
- **BCrypt**: The password hashing algorithm used to store and verify student passwords.
- **NotificationPanel**: The JavaFX UI component that displays live UDP announcements.
- **Department**: One of the known academic department values (e.g., Software Engineering, Computer Science).
- **AcademicLevel**: A top-level grouping of departments (e.g., Freshman, Pre-Engineering).

---

## Requirements

### Requirement 1: Student Authentication via RMI

**User Story:** As a student, I want to log in with my university credentials, so that I can securely access the material sharing system.

#### Acceptance Criteria

1. WHEN a student submits a valid student ID and matching password, THE AuthService SHALL return an AuthResult with `success == true`, a non-null UUID session token, and the populated Student object.
2. WHEN a student submits a student ID that does not exist in the database, THE AuthService SHALL return an AuthResult with `success == false` and a non-null error message, without revealing whether the ID or password was incorrect.
3. WHEN a student submits a correct student ID but an incorrect password, THE AuthService SHALL return an AuthResult with `success == false` and a non-null error message, without revealing whether the ID or password was incorrect.
4. WHEN a login succeeds, THE AuthService SHALL store the session token in the SessionStore mapped to the authenticated Student.
5. THE AuthService SHALL verify passwords by comparing the submitted plaintext against the BCrypt hash stored in the database, and SHALL NOT store or log plaintext passwords at any point.
6. WHEN a student ID or password argument is null or blank, THE AuthService SHALL return an AuthResult with `success == false` and a descriptive error message.

---

### Requirement 2: Session Management

**User Story:** As a student, I want my session to remain valid while I am active and to be invalidated when I log out or time out, so that my account is protected.

#### Acceptance Criteria

1. WHEN a client presents a session token, THE AuthService SHALL return `true` from `validateSession` if and only if the token exists in the SessionStore and has not expired.
2. WHEN a student calls logout with a valid session token, THE AuthService SHALL remove the token from the SessionStore so that subsequent `validateSession` calls for that token return `false`.
3. WHILE a session token has been inactive for more than 2 hours, THE AuthService SHALL treat it as expired and return `false` from `validateSession`.
4. IF a request arrives over TCP with an invalid or expired session token, THEN THE ClientHandler SHALL return an error response without processing the request.

---

### Requirement 3: Material Browsing by Department and Year

**User Story:** As a student, I want to browse materials filtered by department and academic year, so that I can find resources relevant to my courses.

#### Acceptance Criteria

1. WHEN a client calls `getMaterials(department, year)`, THE MaterialService SHALL return a non-null list of Material objects where every element has `material.department` equal to the requested department and `material.year` equal to the requested year.
2. WHEN no materials exist for the given department and year, THE MaterialService SHALL return an empty list rather than null or an error.
3. THE MaterialService SHALL retrieve materials using a parameterized SQL query via JDBC to prevent SQL injection.

---

### Requirement 4: Material Search

**User Story:** As a student, I want to search for materials by keyword, so that I can quickly find specific resources across all departments.

#### Acceptance Criteria

1. WHEN a client calls `searchMaterial(keyword)` with a non-empty keyword, THE MaterialService SHALL return a non-null list of Material objects where every element has `title` or `course` containing the keyword (case-insensitive).
2. WHEN a client calls `searchMaterial("")` with an empty string, THE MaterialService SHALL return all materials in the database ordered by `uploadedAt` descending.
3. THE MaterialService SHALL return search results ordered by `uploadedAt` descending.
4. THE MaterialService SHALL execute the search using a parameterized SQL `LIKE` query via JDBC.

---

### Requirement 5: Material Upload

**User Story:** As a student, I want to upload learning materials with metadata, so that I can share resources with other students.

#### Acceptance Criteria

1. WHEN an authenticated client submits a valid MaterialMetadata and non-empty file bytes, THE MaterialService SHALL write the file to `{storageRoot}/{department}/{year}/{filename}` and insert a corresponding row into the `materials` table, then return an UploadResult with `success == true` and a positive `materialId`.
2. WHEN an upload succeeds, THE UDPBroadcaster SHALL send a broadcast datagram containing the material title and department to all clients on the configured UDP port.
3. IF the file write fails due to an I/O error, THEN THE MaterialService SHALL return an UploadResult with `success == false`, SHALL NOT insert a row into the `materials` table, and SHALL delete any partially written file.
4. IF the database insert fails after a successful file write, THEN THE MaterialService SHALL delete the written file and return an UploadResult with `success == false`, leaving no partial state.
5. WHEN two clients simultaneously upload files with the same filename to the same department and year, THE MaterialService SHALL store both files without conflict by appending a UUID suffix to the storage path.
6. WHEN a client submits an upload request with a null or invalid MaterialMetadata (missing title, course, department, or year), THE MaterialService SHALL return an UploadResult with `success == false` and a descriptive error message.
7. WHEN a client submits an upload request, THE Server SHALL validate the session token before processing the upload, and IF the token is invalid THEN THE Server SHALL reject the request with an authentication error.

---

### Requirement 6: Material Download via TCP

**User Story:** As a student, I want to download learning materials to my local machine, so that I can study them offline.

#### Acceptance Criteria

1. WHEN an authenticated client sends a `DOWNLOAD_REQUEST` with a valid `materialId` over TCP, THE ClientHandler SHALL read the file from the server-side storage path, send a `FILE_HEADER` containing filename and size, then stream the file bytes to the client in chunks, and await a TCP `ACK` from the client.
2. WHEN the client receives the complete file stream, THE Client SHALL write the bytes to local disk.
3. IF the file referenced by `materialId` does not exist on the server file system, THEN THE ClientHandler SHALL return a `DownloadResult` with `success == false` and message "File not available".
4. IF the session token in a download request is invalid or expired, THEN THE ClientHandler SHALL reject the request with an authentication error without reading any file data.
5. THE ClientHandler SHALL stream file data in chunks of 8 KB rather than loading the entire file into memory.

---

### Requirement 7: TCP Server and Multithreaded Client Handling

**User Story:** As a system operator, I want the server to handle multiple concurrent client connections, so that many students can use the system simultaneously.

#### Acceptance Criteria

1. WHEN the TCPServer starts, THE TCPServer SHALL accept incoming connections on the configured TCP port and submit each accepted socket to a fixed thread pool for processing.
2. THE TCPServer SHALL use a fixed thread pool sized to `min(32, availableProcessors + 4)` to bound resource usage.
3. WHEN a ClientHandler processes a connection, THE ClientHandler SHALL read requests and write responses in a loop until the client disconnects or the socket is closed.
4. WHEN a client disconnects cleanly (EOF), THE ClientHandler SHALL close the socket and release all associated resources without throwing an unhandled exception.
5. IF an unexpected I/O or deserialization error occurs in a ClientHandler, THEN THE ClientHandler SHALL log the error, close the socket, and terminate that handler thread without affecting other active connections.
6. WHEN the ClientHandler's `run()` method returns for any reason, THE ClientHandler SHALL ensure the client socket is closed.

---

### Requirement 8: RMI Service Registration and Invocation

**User Story:** As a developer, I want the server to expose authentication and material services over RMI, so that clients can invoke remote methods transparently.

#### Acceptance Criteria

1. WHEN the Server starts, THE Server SHALL create an RMI registry on the configured RMI port and bind the `UniversityRMIService` implementation under the name `"UniversityService"`.
2. WHEN a client performs an RMI lookup for `"UniversityService"`, THE Client SHALL receive a stub that delegates calls to the server-side `UniversityRMIServiceImpl`.
3. THE UniversityRMIServiceImpl SHALL delegate all method calls to the shared BusinessLogic layer.
4. IF the client cannot reach the RMI registry, THEN THE Client SHALL catch the `RemoteException`, display a "Connecting..." indicator, and retry the RMI lookup after 5 seconds.
5. WHERE RMI is unavailable after retries, THE Client SHALL fall back to TCP socket-based requests for the same operation.

---

### Requirement 9: UDP Broadcast Notifications

**User Story:** As a student, I want to receive real-time notifications when new materials are uploaded, so that I am aware of new resources without manually refreshing.

#### Acceptance Criteria

1. WHEN a material upload succeeds, THE UDPBroadcaster SHALL send a `DatagramPacket` to the broadcast address on the configured UDP port containing the message `"New material: {title} in {department}"`.
2. WHEN the UDPListener receives a datagram, THE UDPListener SHALL invoke the registered `NotificationHandler` callback with the message string.
3. WHEN the NotificationHandler callback is invoked, THE Client SHALL display the notification in the NotificationPanel using `Platform.runLater` to ensure the update occurs on the JavaFX application thread.
4. THE UDPListener SHALL run as a daemon thread so that it does not prevent JVM shutdown.
5. UDP broadcast messages SHALL NOT contain any session tokens, passwords, or other sensitive data.

---

### Requirement 10: JavaFX Client UI Navigation

**User Story:** As a student, I want a clear and navigable UI, so that I can move between login, browsing, uploading, and notifications without confusion.

#### Acceptance Criteria

1. WHEN the application starts, THE Client SHALL display the LoginScreen as the initial screen.
2. WHEN login succeeds, THE Client SHALL navigate to the DashboardScreen and store the session token and Student object in the SessionContext.
3. WHEN a student selects an AcademicLevel on the DashboardScreen, THE Client SHALL navigate to the DepartmentScreen showing departments for that level.
4. WHEN a student selects a Department, THE Client SHALL navigate to the YearScreen showing available years (2nd, 3rd, 4th, GC).
5. WHEN a student selects a Year, THE Client SHALL navigate to the MaterialScreen and call `getMaterials(department, year)` to populate the material list.
6. WHEN a student clicks the upload button on the MaterialScreen, THE Client SHALL navigate to the UploadScreen.
7. WHEN a student submits the upload form on the UploadScreen with a selected file and valid metadata, THE Client SHALL call the upload service and navigate back to the MaterialScreen on success.
8. IF login fails, THEN THE Client SHALL display the error message from AuthResult on the LoginScreen without navigating away.

---

### Requirement 11: Server Startup and Configuration

**User Story:** As a system operator, I want the server to initialize all components from a configuration, so that I can deploy and configure the system without recompiling.

#### Acceptance Criteria

1. WHEN the Server starts with a valid ServerConfig, THE Server SHALL initialize the JDBC connection pool, start the RMI registry, bind the RMI service, start the TCP listener, and initialize the UDPBroadcaster before accepting any client connections.
2. THE Server SHALL read TCP port, RMI port, UDP port, maximum thread count, database URL, database credentials, and storage root path from the ServerConfig.
3. IF the database is unreachable at startup, THEN THE Server SHALL log the error and terminate with a non-zero exit code.
4. IF the configured TCP or RMI port is already in use, THEN THE Server SHALL log the conflict and terminate with a non-zero exit code.

---

### Requirement 12: Database Persistence and Schema

**User Story:** As a system operator, I want all student and material data persisted in MySQL, so that data survives server restarts.

#### Acceptance Criteria

1. THE System SHALL persist student records in a `students` table with columns: `student_id` (VARCHAR PK), `name`, `password` (BCrypt hash), `department`, and `year`.
2. THE System SHALL persist material records in a `materials` table with columns: `material_id` (INT PK AUTO_INCREMENT), `title`, `course`, `department`, `year`, `file_path`, `uploaded_by` (FK to students), and `uploaded_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP).
3. THE System SHALL persist announcement records in an `announcements` table with columns: `id` (INT PK AUTO_INCREMENT), `message`, and `date` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP).
4. THE DAO SHALL use `PreparedStatement` with parameterized inputs for all SQL queries to prevent SQL injection.
5. IF a JDBC operation fails, THEN THE DAO SHALL throw a `ServiceException` wrapping the `SQLException`, and THE Server SHALL return an error response to the client.
6. WHEN a JDBC connection fails, THE Server SHALL retry the connection up to 3 times with exponential backoff before returning an error.

---

### Requirement 13: Security Controls

**User Story:** As a system operator, I want the system to enforce security controls, so that student data and files are protected from unauthorized access and attacks.

#### Acceptance Criteria

1. THE System SHALL store all student passwords as BCrypt hashes with a cost factor of at least 10, and SHALL NOT store, log, or transmit plaintext passwords.
2. THE AuthService SHALL issue session tokens as UUID v4 strings and SHALL expire tokens after 2 hours of inactivity.
3. THE DAO SHALL use parameterized `PreparedStatement` queries for all database interactions to prevent SQL injection.
4. WHEN constructing a file storage path from client-supplied metadata, THE Server SHALL sanitize the filename by removing `..`, `/`, and `\` characters to prevent path traversal attacks.
5. THE Server SHALL reject any upload or download request that does not include a valid, non-expired session token.
6. UDP broadcast messages SHALL contain only material title and department — no session tokens, passwords, file paths, or student IDs.

---

### Requirement 14: File Storage

**User Story:** As a developer, I want files stored in an organized directory structure on the server, so that they can be reliably retrieved and managed.

#### Acceptance Criteria

1. WHEN a file is uploaded, THE Server SHALL store it at the path `{storageRoot}/{department}/{year}/{filename}`, creating intermediate directories if they do not exist.
2. WHEN two files with the same name are uploaded to the same department and year, THE Server SHALL append a UUID suffix to the second filename to ensure uniqueness.
3. WHEN a file is downloaded, THE Server SHALL read it using Java NIO streams in 8 KB chunks.
4. IF a file referenced in the database does not exist on the file system, THEN THE Server SHALL log the discrepancy and return a `DownloadResult` with `success == false`.
