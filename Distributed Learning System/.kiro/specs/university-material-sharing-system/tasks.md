# Implementation Plan: Distributed University Student Learning Material Sharing System

## Overview

Implement a Java-based distributed client-server application using Maven, JavaFX, TCP/UDP sockets, RMI, JDBC (MySQL), and BCrypt. Tasks are ordered so each step compiles and integrates with the previous one, ending with a fully wired application.

## Tasks

- [~] 1. Set up Maven project structure and dependencies
  - Create `pom.xml` with modules/packages for `common`, `server`, `client`, `dao`, `service`, `network`, `ui`
  - Add dependencies: JavaFX 17, MySQL Connector/J 8.x, jBCrypt, JUnit 5, junit-quickcheck, H2
  - Create `src/main/java` and `src/test/java` directory trees with package stubs
  - Create `src/main/resources/schema.sql` with the three-table DDL (students, materials, announcements)
  - _Requirements: 11.2, 12.1, 12.2, 12.3_

- [ ] 2. Implement common model and transfer objects
  - [~] 2.1 Create `Student`, `Material`, `Announcement` model classes with all fields, getters/setters, and `Serializable`
    - Enforce validation rules: non-null studentId, year in {1,2,3,4}, non-empty title/course
    - _Requirements: 12.1, 12.2_
  - [~] 2.2 Create `MaterialMetadata`, `AuthResult`, `UploadResult` serializable transfer objects
    - Include static factory methods `AuthResult.success(token, student)` and `AuthResult.failure(message)`
    - Include `UploadResult.success(materialId)` and `UploadResult.failure(message)`
    - _Requirements: 1.1, 5.1_
  - [~] 2.3 Create `Request` and `Response` serializable envelope classes with a `type` enum (`LOGIN`, `SEARCH`, `GET_MATERIALS`, `UPLOAD`, `DOWNLOAD`, `LOGOUT`)
    - _Requirements: 7.3_
  - [~] 2.4 Create `ServiceException` (checked) wrapping `SQLException`
    - _Requirements: 12.5_

- [ ] 3. Implement database layer
  - [~] 3.1 Implement `DatabasePool` using a simple `HikariCP`-style or manual `DriverManager` pool that retries up to 3 times with exponential backoff
    - _Requirements: 11.1, 12.6_
  - [ ] 3.2 Implement `StudentDAO` with `findById(studentId)` using `PreparedStatement`
    - Throw `ServiceException` on `SQLException`
    - _Requirements: 12.4, 12.5, 13.3_
  - [ ] 3.3 Implement `MaterialDAO` with `insert(metadata, filePath)`, `findByDepartmentAndYear(dept, year)`, `findById(id)`, and `search(keyword)` using parameterized queries
    - `search` uses `LIKE` with `%keyword%` pattern; orders by `uploaded_at DESC`
    - Throw `ServiceException` on `SQLException`
    - _Requirements: 3.3, 4.4, 12.4, 12.5_
  - [ ]* 3.4 Write unit tests for `StudentDAO` and `MaterialDAO` using H2 in-memory database
    - Test `findById` for existing and missing records
    - Test `insert` and `findByDepartmentAndYear` round trip
    - Test `search` with keyword, empty string, and no-match cases
    - _Requirements: 3.1, 4.1, 12.4_

- [ ] 4. Implement business logic and authentication service
  - [ ] 4.1 Implement `AuthServiceImpl` with `login`, `validateSession`, and `logout`
    - Use `BCrypt.checkpw` for password verification (cost factor ≥ 10 on insert)
    - Store tokens in `ConcurrentHashMap<String, SessionEntry>` where `SessionEntry` holds `Student` + last-access timestamp
    - Expire tokens after 2 hours of inactivity in `validateSession`
    - Return generic error message for both "not found" and "wrong password" cases
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 2.1, 2.2, 2.3, 13.1, 13.2_
  - [ ]* 4.2 Write property test for `AuthServiceImpl` login correctness (Property 1)
    - **Property 1: Login result correctness**
    - **Validates: Requirements 1.1, 1.2, 1.3**
  - [ ]* 4.3 Write property test for login–validateSession round trip (Property 2)
    - **Property 2: Login–validateSession round trip**
    - **Validates: Requirements 1.4, 2.1**
  - [ ]* 4.4 Write property test for logout invalidates session (Property 3)
    - **Property 3: Logout invalidates session**
    - **Validates: Requirements 2.2**
  - [ ]* 4.5 Write unit tests for `AuthServiceImpl`
    - Test correct credentials, wrong password, unknown student ID, null/blank inputs
    - _Requirements: 1.1, 1.2, 1.3, 1.6_
  - [ ] 4.6 Implement `MaterialServiceImpl` with `getMaterials`, `searchMaterial`, `uploadMaterial`, and `downloadMaterial`
    - `uploadMaterial`: sanitize filename (strip `..`, `/`, `\`), build path `{storageRoot}/{dept}/{year}/{filename}`, append UUID suffix on collision, write file, insert DB row, trigger UDP broadcast; rollback file on any failure
    - `downloadMaterial`: read file in 8 KB chunks via NIO
    - _Requirements: 3.1, 3.2, 4.1, 4.2, 4.3, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 6.1, 13.4, 14.1, 14.2, 14.3_
  - [ ]* 4.7 Write property test for getMaterials filter correctness (Property 5)
    - **Property 5: getMaterials filter correctness**
    - **Validates: Requirements 3.1**
  - [ ]* 4.8 Write property test for searchMaterial filter correctness (Property 6)
    - **Property 6: searchMaterial filter correctness**
    - **Validates: Requirements 4.1**
  - [ ]* 4.9 Write property test for search results ordering (Property 7)
    - **Property 7: Search results ordering**
    - **Validates: Requirements 4.3**
  - [ ]* 4.10 Write property test for upload–getMaterials round trip (Property 8)
    - **Property 8: Upload–getMaterials round trip**
    - **Validates: Requirements 5.1**
  - [ ]* 4.11 Write property test for upload atomicity on failure (Property 9)
    - **Property 9: Upload atomicity on failure**
    - **Validates: Requirements 5.3, 5.4**
  - [ ]* 4.12 Write property test for concurrent upload uniqueness (Property 10)
    - **Property 10: Concurrent upload uniqueness**
    - **Validates: Requirements 5.5**
  - [ ]* 4.13 Write unit tests for `MaterialServiceImpl`
    - Test successful upload, I/O failure rollback, DB failure rollback, invalid metadata
    - _Requirements: 5.1, 5.3, 5.4, 5.6_
  - [ ] 4.14 Implement `BusinessLogic` facade that wires `AuthServiceImpl`, `MaterialServiceImpl`, `StudentDAO`, `MaterialDAO`, and `UDPBroadcaster` together and exposes a `handle(Request)` method dispatching by request type
    - Validate session token for all non-login requests; return error `Response` if invalid
    - _Requirements: 2.4, 5.7, 6.4, 7.3_
  - [ ]* 4.15 Write property test for invalid session token rejection (Property 4)
    - **Property 4: Invalid session token rejection**
    - **Validates: Requirements 2.4**
  - [ ]* 4.16 Write property test for JDBC failure produces ServiceException (Property 15)
    - **Property 15: JDBC failure produces ServiceException**
    - **Validates: Requirements 12.5**
  - [ ]* 4.17 Write property test for BCrypt hash cost factor (Property 16)
    - **Property 16: BCrypt hash cost factor**
    - **Validates: Requirements 13.1**
  - [ ]* 4.18 Write property test for filename sanitization (Property 17)
    - **Property 17: Filename sanitization prevents path traversal**
    - **Validates: Requirements 13.4**
  - [ ]* 4.19 Write property test for storage path construction (Property 18)
    - **Property 18: Storage path construction**
    - **Validates: Requirements 14.1**

- [ ] 5. Checkpoint — Ensure all DAO and service tests pass
  - Run `mvn test -pl .` scoped to `dao` and `service` packages; fix any failures before proceeding.

- [ ] 6. Implement UDP broadcaster and listener
  - [ ] 6.1 Implement `UDPBroadcaster.broadcast(message, port)` sending a `DatagramPacket` to the broadcast address
    - Message format: `"New material: {title} in {department}"` — no tokens, passwords, or file paths
    - _Requirements: 9.1, 9.5, 13.6_
  - [ ] 6.2 Implement `UDPListener implements Runnable` that blocks on `DatagramSocket.receive()` and invokes the `NotificationHandler` callback with the decoded message string
    - Set thread as daemon so it does not block JVM shutdown
    - _Requirements: 9.2, 9.4_
  - [ ]* 6.3 Write property test for UDP listener callback delivery (Property 13)
    - **Property 13: UDP listener callback delivery**
    - **Validates: Requirements 9.2**
  - [ ]* 6.4 Write property test for UDP broadcast message safety (Property 14)
    - **Property 14: UDP broadcast message safety**
    - **Validates: Requirements 9.5, 13.6**

- [ ] 7. Implement TCP server and client handler
  - [ ] 7.1 Implement `TCPServer` with `start(port)` that accepts connections and submits each `Socket` to a fixed thread pool sized `min(32, availableProcessors + 4)`
    - _Requirements: 7.1, 7.2_
  - [ ] 7.2 Implement `ClientHandler implements Runnable` with the request-response loop using `ObjectInputStream`/`ObjectOutputStream`
    - Handle `EOFException` as clean disconnect; log and close on other exceptions
    - Ensure socket is closed in `finally` block regardless of exit path
    - Validate session token before processing any non-login request
    - _Requirements: 2.4, 6.4, 7.3, 7.4, 7.5, 7.6_
  - [ ] 7.3 Implement chunked file streaming in `ClientHandler` for `DOWNLOAD` requests: send `FILE_HEADER` (filename + size), then stream file in 8 KB chunks, await `ACK`
    - _Requirements: 6.1, 6.5, 14.3_
  - [ ]* 7.4 Write property test for ClientHandler socket lifecycle (Property 12)
    - **Property 12: ClientHandler socket lifecycle**
    - **Validates: Requirements 7.4, 7.5**
  - [ ]* 7.5 Write unit tests for `ClientHandler`
    - Test clean disconnect (EOFException), malformed request, valid request cycle, invalid session rejection
    - _Requirements: 7.3, 7.4, 7.5_

- [ ] 8. Implement RMI interfaces and server-side implementation
  - [ ] 8.1 Define `UniversityRMIService extends Remote` interface with `login`, `getMaterials`, `searchMaterial`, and `uploadMaterial` methods throwing `RemoteException`
    - _Requirements: 8.1, 8.2_
  - [ ] 8.2 Implement `UniversityRMIServiceImpl extends UnicastRemoteObject implements UniversityRMIService` delegating all calls to `BusinessLogic`
    - _Requirements: 8.3_

- [ ] 9. Implement server configuration and startup
  - [ ] 9.1 Create `ServerConfig` POJO with fields: `tcpPort`, `rmiPort`, `udpPort`, `maxThreads`, `dbUrl`, `dbUser`, `dbPassword`, `storageRoot`; load from `server.properties` file
    - _Requirements: 11.2_
  - [ ] 9.2 Implement `ServerMain` following the startup sequence: init DB pool → build `BusinessLogic` → create RMI registry and bind service → start `TCPServer` with thread pool
    - Log and exit with non-zero code if DB is unreachable or ports are in use
    - _Requirements: 11.1, 11.3, 11.4_

- [ ] 10. Checkpoint — Verify server starts and accepts connections
  - Write a smoke-test that starts `ServerMain` against H2, connects a TCP client, sends a `LOGIN` request, and asserts a valid `AuthResult` is returned.

- [ ] 11. Implement client networking layer
  - [ ] 11.1 Implement `SessionContext` as a thread-safe singleton holding the current `sessionToken` and `Student`
    - _Requirements: 10.2_
  - [ ] 11.2 Implement `RMIClient` that performs `LocateRegistry.getRegistry` lookup for `"UniversityService"`, retries every 5 seconds on `RemoteException`, and shows a "Connecting..." state
    - _Requirements: 8.4_
  - [ ] 11.3 Implement `TCPClient` with methods for each request type (`login`, `search`, `getMaterials`, `upload`, `download`) that serialize a `Request` and deserialize the `Response`
    - Used as fallback when RMI is unavailable
    - _Requirements: 8.5_
  - [ ] 11.4 Implement `DownloadManager` that receives the `FILE_HEADER` + chunked byte stream from the TCP server and writes the file to local disk
    - _Requirements: 6.2_

- [ ] 12. Implement JavaFX client UI screens
  - [ ] 12.1 Create `ClientApp extends Application` as the JavaFX entry point; initialize `UDPListener` as a daemon thread and wire `NotificationHandler` to `NotificationPanel`
    - _Requirements: 10.1, 9.3, 9.4_
  - [ ] 12.2 Implement `LoginScreen` with student ID and password fields; on submit call `RMIClient.login`, store result in `SessionContext`, navigate to `DashboardScreen` on success, show error on failure
    - _Requirements: 10.1, 10.2, 10.8_
  - [ ] 12.3 Implement `DashboardScreen` showing academic level buttons (Freshman, Pre-Engineering); on selection navigate to `DepartmentScreen`
    - _Requirements: 10.3_
  - [ ] 12.4 Implement `DepartmentScreen` showing department list for the selected level; on selection navigate to `YearScreen`
    - _Requirements: 10.4_
  - [ ] 12.5 Implement `YearScreen` showing year options (2nd, 3rd, 4th, GC); on selection navigate to `MaterialScreen` and call `getMaterials(department, year)`
    - _Requirements: 10.5_
  - [ ] 12.6 Implement `MaterialScreen` with a `TableView<Material>`, search bar (calls `searchMaterial`), download button (calls `TCPClient.download`), and upload button navigating to `UploadScreen`
    - _Requirements: 10.5, 10.6_
  - [ ] 12.7 Implement `UploadScreen` with a file picker and metadata form (title, course, department, year); on submit call upload service and navigate back to `MaterialScreen` on success
    - _Requirements: 10.7_
  - [ ] 12.8 Implement `NotificationPanel` as a `VBox` or `ListView` that appends notification strings via `Platform.runLater`
    - _Requirements: 9.3_

- [ ] 13. Wire everything together and integration tests
  - [ ] 13.1 Wire `ClientApp` to use `RMIClient` with TCP fallback: if `RMIClient` throws after retries, delegate the same call to `TCPClient`
    - _Requirements: 8.4, 8.5_
  - [ ]* 13.2 Write integration test for full login → navigate → download flow using H2 in-memory database
    - _Requirements: 1.1, 6.1, 10.2_
  - [ ]* 13.3 Write integration test for TCP client-server round trip: send `SEARCH` request, verify response matches DB state
    - _Requirements: 4.1, 7.3_
  - [ ]* 13.4 Write integration test for UDP broadcast: trigger upload, verify all registered `UDPListener` instances receive the announcement within 500 ms
    - _Requirements: 9.1, 9.2_
  - [ ]* 13.5 Write property test for download round trip (Property 11)
    - **Property 11: Download round trip**
    - **Validates: Requirements 6.1**

- [ ] 14. Final checkpoint — Ensure all tests pass
  - Run `mvn test`; fix any remaining failures. Confirm the server starts cleanly and the JavaFX client launches to the LoginScreen.

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- Each task references specific requirements for traceability
- Checkpoints (tasks 5, 10, 14) ensure incremental validation at key integration boundaries
- Property tests use `junit-quickcheck`; unit tests use JUnit 5
- H2 is used for all automated tests; MySQL is the production database
- The `BusinessLogic.handle(Request)` dispatcher is the single integration point between TCP/RMI and the service layer
