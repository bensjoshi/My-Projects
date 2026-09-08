

# My Portfolio
# Hi there, I'm Ben Joshi. This is my software portfolio.

## Dorm Room Application
**A distributed room-booking system with a REST API backend and a standalone console client**

### Overview
A two-tier system for browsing and applying for student accommodation, built to practice proper client-server separation rather than a single monolithic app. The backend is a Java EE RESTful web service; the client is an entirely separate Java console application that only ever talks to the backend over HTTP.

### Architecture & Tech Stack
- **Backend:** Java EE / JAX-RS (Jersey), deployed as a servlet-container web app
- **Client:** Standalone Java console app, communicating via `HttpURLConnection`
- **Serialization:** Gson, independently on both sides of the API boundary
- **External integrations:** OSRM (Open Source Routing Machine) for driving-distance calculation between coordinates; 7Timer! for weather forecasts
- **Persistence:** File-backed JSON (no database)

### Key Technical Highlights
- Designed a REST API with resources for rooms, applications, distance, and weather — each a separate JAX-RS `@Path` class registered through a central `ApplicationConfig`
- Integrated two third-party public APIs server-side: parsing OSRM's GeoJSON route response to extract distance, and deserializing 7Timer's nested weather payload (`dataseries[].temp2m.{max,min}`) into typed Java objects
- Implemented server-side availability logic using `java.time.LocalDate` comparisons rather than trusting client-supplied flags
- Built the client as a fully independent consumer of the API — its own DTOs, its own HTTP handling — to practice the discipline of designing a backend for consumers you don't control

### What I'd Improve
The file-backed JSON persistence works but isn't concurrency-safe — application writes use manual string manipulation to keep a JSON array valid across appends. A real database (even SQLite) would remove that fragility entirely. I'd also extract the hardcoded file paths into configuration and share DTOs between client and server via a common module instead of duplicating them.

---

## FutureFridges
**A multi-role kitchen inventory and operations app for commercial food service teams**

### Overview
An Android app supporting four distinct staff roles — Head Chef, Regular Chef, Delivery Person, and Manager — each with their own login flow and dashboard, built around a shared Firestore backend. It handles inventory tracking, expiry/low-stock alerting, order fulfillment, delivery history, and health & safety reporting.

### Architecture & Tech Stack
- **Platform:** Android (Java), Firebase/Firestore backend
- **Structure:** Single app, four role-specific Activity/Fragment flows branching from a shared entry point
- **Data:** Firestore collections for employees, food inventory, orders, deliveries, reports, and notifications

### Key Technical Highlights
- Built a role-based access structure across four distinct user types sharing one data backend, each with scoped views and permissions
- Implemented expiry-window and low-stock detection logic: inventory items are checked against a rolling time window and quantity threshold on each fetch, automatically generating notification records when thresholds are crossed
- Designed a decoupled alerting pattern — detection logic and notification delivery are separate concerns, with alerts persisted as their own Firestore collection rather than being ephemeral UI state
- Delivered a genuinely full CRUD feature set — inventory, ordering, delivery history, compliance reporting, and manager-side user administration — across 30+ source files

### What I'd Improve
Authentication is currently custom-built against a Firestore `employees` collection rather than using Firebase Authentication, and credentials aren't hashed — functional for the coursework scope, but the clear next step for production would be migrating to Firebase Auth with custom claims for roles and proper credential hashing. I'd also move expiry-checking server-side via a scheduled Cloud Function so alerts fire in real time rather than only when a user opens the inventory screen.

---

## PropertyApp
**A property management app for landlords, with federated authentication**

### Overview
An Android app for landlords to list, update, and track rental properties — tenant details, rent and deposit amounts, tenancy dates, and property photos — scoped per-user so each landlord only sees their own portfolio.

### Architecture & Tech Stack
- **Platform:** Android (Java), Firebase backend
- **Auth:** Firebase Authentication with email/password, plus Google Sign-In federation (OAuth token exchange into a Firebase credential)
- **Data:** Firestore for property records, local app storage for property photos

### Key Technical Highlights
- Implemented proper federated authentication: Google Sign-In returns an ID token, which is exchanged for a Firebase credential via `GoogleAuthProvider`, giving users a one-tap login alongside standard email/password
- Every property record is written with the owner's Firebase UID attached, enabling per-user data isolation enforceable through Firestore security rules
- Built image capture and local persistence: photos are picked from the device gallery, decoded, compressed, and written to app-internal storage, with only the file reference persisted to Firestore

### What I'd Improve
Property photos are currently stored on-device rather than in Firebase Storage, so they don't sync across devices or survive a reinstall — migrating to Storage with a persisted download URL would fix that properly. Numeric fields like rent and deposit are also stored as strings; moving them to proper numeric types would allow server-side filtering and sorting (e.g. "properties under £X").

---

## SoundWave
**A social networking app for musicians, with a transactional social graph**

### Overview
My final year university project — a social media app connecting musicians through shared instruments, genres, and interests, with a content feed, profile discovery, and a full follow/unfollow social graph.

### Architecture & Tech Stack
- **Platform:** Android (Java), Firebase/Firestore backend
- **Core feature:** Bidirectional follow relationships with denormalized counters, kept consistent via Firestore transactions

### Key Technical Highlights
- Implemented follow/unfollow using a **Firestore transaction** that reads both users' documents, then atomically updates `following`/`followers` arrays (via `arrayUnion`/`arrayRemove`) and denormalized `followingCount`/`followersCount` fields (via `increment`) on both sides in a single atomic operation — the correct pattern for keeping a bidirectional relationship and its counters consistent under concurrent writes, avoiding race conditions that plain sequential updates would be vulnerable to
- Built a content feed, profile discovery, search, and full profile editing (instruments, genres, bio, location) on a shared Firestore schema
- Designed the user profile schema with tagged fields (instruments and genres as list types) specifically to support future similarity-based matching

### What I'd Improve
The discovery/matching feature currently selects a musician at random from the full user base (with simple repeat-avoidance) rather than ranking by shared interests — an honest limitation worth naming, and a natural next step given the schema already captures instruments and genres. I'd also replace the full-collection fetch on each discovery request with paginated queries as the user base grows, since pulling every user document per request doesn't scale.

---

## Musical Instruments Chatbot
**A multi-modal AI system combining a CNN classifier, symbolic reasoning, and information retrieval**

### Overview
A conversational assistant for brass/woodwind instrument questions that routes between five distinct AI techniques depending on the type of input it receives — image, greeting, factual assertion, factual query, or open question — rather than relying on a single model.

### Architecture & Tech Stack
- **Image classification:** Convolutional neural network (Keras/TensorFlow), architecture selected via automated hyperparameter search (Keras Tuner's Hyperband algorithm) across filter counts, dropout rate, dense layer width, and learning rate
- **Conversational layer:** AIML (Artificial Intelligence Markup Language) pattern-matching kernel for greetings and small talk
- **Question answering:** TF-IDF vectorization with cosine similarity for retrieval-based Q&A matching
- **Knowledge representation:** A symbolic subject-predicate-object fact store supporting new fact insertion and fuzzy-matched contradiction checking
- **Speech I/O:** Speech-to-text and offline text-to-speech for voice-driven interaction

### Key Technical Highlights
- Trained a CNN for binary brass-vs-woodwind image classification, with the network architecture itself found through Hyperband search rather than manually tuned — a genuinely more rigorous approach than fixed hyperparameters
- Built a symbolic knowledge base that persists new facts back to disk across sessions, with contradiction detection using fuzzy string matching (rather than brittle exact-match comparison) so near-equivalent phrasings are still recognized correctly
- Designed a rule-based orchestration layer that inspects each user input and routes it to the appropriate subsystem — image classifier, AIML kernel, KB writer, KB checker, or TF-IDF retrieval — making the system's reasoning explainable rather than a black box
- Integrated five materially different AI/NLP techniques into one coherent interaction loop, including optional voice input/output

### What I'd Improve
The knowledge base and Q&A datasets are intentionally small, built to demonstrate each technique working correctly rather than to scale to a production knowledge base — a real deployment would need a much larger, curated fact set. I'd also look at replacing the manual keyword-based input router with a lightweight intent classifier, so routing decisions are learned rather than hardcoded.
