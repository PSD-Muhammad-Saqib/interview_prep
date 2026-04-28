# Systems Limited — 1-Day Interview Prep (Revision Doc)

> **Your stack (from resume):** Java Spring Boot, Spring Data JPA, Spring Security, Spring Batch, Spring Cloud, React + TypeScript + Redux, MS SQL / MySQL / MongoDB, Docker on Azure VM (DB + **RabbitMQ** + microservices), Microsoft Fabric, integrations (Xero, Sage 200, QuickBooks, Nourish).  
> **Flagship projects to name:** **RodaBI** (care + accounting), **Dealdiva** (MVP Spring + React, RBAC, JWT), **Fitpair** (MEAN).

---

## My resume hooks (use verbatim in interview)
- **Spring Boot + DB:** NetSol **RodaBI** — care monitoring & financial reporting; Spring Boot + JPA + complex SQL/reporting; roles & permissions; real-time notifications; **MS SQL Server** (and Azure SQL per skills).
- **React (TS):** RodaBI, Dealdiva — component UI, Redux; Dealdiva: protected routes, admin panel, ratings/voting, referral/cashback flows.
- **Microservices / messaging / deploy:** Microservices architecture + **Spring Cloud**; **Docker** env on **Azure VM** with **RabbitMQ** + DB + services for dev/test; REST APIs across services.
- **Security:** Spring Security + **token-based** login, **RBAC** (Dealdiva; also “secure APIs” at incubation center).
- **Integrations:** External care/finance: **Xero, Sage 200, QuickBooks, Nourish** — sync, mapping, error handling, idempotency (be ready to explain *one* flow).
- **Batch / data:** **Spring Batch** + **Microsoft Fabric** for external data processing and insights into internal apps.
- **Leadership:** Mentoring juniors via **code reviews** and knowledge-sharing (align with “how you manage juniors” question).

---

## Where they’ll probe deeper (your profile)
| Area | Why (resume) | Be ready with |
|------|----------------|---------------|
| **Spring Security + JWT** | Dealdiva + incubation | Full filter chain, where token is validated, roles in claims vs DB, `@PreAuthorize` |
| **Microservices + RabbitMQ** | You used RabbitMQ on Azure | Exchanges/queues vs Kafka log; dead-letter; retry; when message broker beats sync HTTP |
| **Spring Cloud** | Listed explicitly | Config, discovery (Eureka/Consul), gateway — say what you *actually* used, not buzzwords |
| **Spring Batch** | Listed | Chunk-oriented processing, job/repository, retries, scheduling |
| **JPA / SQL** | “Complex SQL” + JPA | N+1, lazy fetch joins, `@Query`, indexes for reports |
| **Optimization (~40%)** | Strong claim | **STAR + numbers** (before/after latency or query time, what exactly changed) |
| **Factory pattern “in our code”** | Common SL question | **Integration clients:** factory/strategy picking Xero vs Sage vs QuickBooks adapter; or `NotificationChannel` by type |
| **Docker + Azure** | VM compose stack | Images, networking, env files, how RabbitMQ + DB + services wire together |
| **React** | Redux + TS | `useEffect` deps, cancel fetch, props vs context vs Redux |

---

## Time-boxed plan (1 day)

| Block | Time | Focus | Why high ROI |
|-------|------|--------|--------------|
| A | 2h | JVM memory (heap/stack), GC generations, String pool, equals/hashCode | They repeat this |
| B | 2h | Spring: DI/IoC, beans, `@RestController`, Security + JWT flow, exceptions | Core backend |
| C | 1.5h | JPA: lazy/eager, `@JoinColumn`/`@JoinTable`, custom queries | Deep Hibernate Qs |
| D | 1.5h | Streams: map/flatMap, terminal vs intermediate, short-circuit, **coding** | Many trick Qs |
| E | 1h | SQL: joins, HAVING, indexes, GROUP BY counts, ACID | Practical |
| F | 1h | Microservices: gateway, resilience, Saga, Kafka vs Rabbit, alternatives | Architecture |
| G | 1h | React: hooks, promises/async, cancel fetch, CORS, caching | “Other” round |
| H | 30m | Multithreading, CHM vs HashMap, singleton thread-safety | Concurrency |
| I | 30m | SDLC, SOLID + 1 example, Factory/Strategy, leadership Q | Behavioral + patterns |

**Pitfalls they probe:** stream return types; `map` vs `flatMap`; parent/child reference rules; overriding equals/hashCode; lazy loading N+1; JWT verify vs decode; “what if service is down.”

**Quick revision:** One A4 “cheat sheet” per block: 10 bullets + 1 tiny code snippet each.

---

## Java Streams — patterns (core)

- **Source** → **intermediate** (lazy) → **terminal** (eager execution).
- **map**: 1→1; **flatMap**: 1→many flattened (`Stream<R>` from `Stream<Stream<R>>`).
- **filter**, **distinct**, **sorted**, **peek** (debug).
- **reduce** (aggregate), **collect** (`groupingBy`, `partitioningBy`, `joining`).
- **Short-circuit:** `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`, `limit` — may stop pipeline early.

---

## Streams — 8 practice problems (solutions: mental model)

1. **Sum of squares of even numbers**  
   `ints.stream().filter(n -> n % 2 == 0).map(n -> n * n).reduce(0, Integer::sum)`

2. **Flatten list of lists**  
   `lists.stream().flatMap(List::stream)`

3. **Words → frequency map**  
   `Collectors.groupingBy(w -> w, Collectors.counting())`

4. **Top N salaries**  
   `employees.stream().sorted(Comparator.comparing(E::getSalary).reversed()).limit(n)`

5. **Partition adults / minors**  
   `Collectors.partitioningBy(p -> p.getAge() >= 18)`

6. **Distinct by email (custom)**  
   `filter` with `Set.seen` in custom collector, or `Collectors.toMap(Email, e -> e, (a,b)->a)` then values.

7. **First failing validation (short-circuit)**  
   `rules.stream().filter(r -> !r.test(x)).findFirst()`

8. **Join names with comma**  
   `names.stream().collect(Collectors.joining(", "))`

**Thought process:** Choose *shape* of data (scalar / list / map), pick terminal op last, avoid repeated stream traversal if you need multiple aggregates → use `teeing` or one `collect` with custom collector.

---

## Micro story for “optimization on resume” (~40% claim)

**Template (fill real numbers before interview):**  
- **S:** Module or report in **RodaBI** / core service was slow (dashboard load / batch / API).  
- **T:** Reduce latency or DB load; pay down legacy debt.  
- **A:** Name **2–3** concrete actions, e.g. (pick what you did): removed **N+1** (entity graphs / batch fetch / `@EntityGraph`), added **indexes** for report queries, **paginated** large lists, **cached** read-heavy reference data, refactored **hot paths** in Spring services, reduced payload size on React side, **Spring Batch** tuning (chunk size / parallel steps), SQL rewrite (avoid subquery → join).  
- **R:** “~40%” → tie to **one metric**: e.g. p95 API from X→Y ms, report runtime, or fewer DB round-trips per request.

**If pressed:** “40% was cumulative across several fixes in core modules, not a single line change.” Stay honest if they ask for proof.

---

# ANSWER SHEET — ALL QUESTIONS (bullets)

### OOP / general
- **How OOP made life easy?** Modeling domain as objects, reuse via inheritance/composition, encapsulation hides complexity, polymorphism lets you substitute implementations.
- **Abstraction:** Hide “how,” expose “what” (interfaces, abstract classes).
- **Encapsulation:** Private fields + accessors; invariant protection.
- **Inheritance vs composition:** Inheritance “is-a” (can be wrong for reuse); composition “has-a” (flexible, testable). Prefer composition for behavior reuse.
- **Runtime vs compile-time polymorphism:** Overriding (dynamic dispatch) vs overloading (resolved at compile time).
- **Why not multiple class inheritance?** Diamond problem, ambiguity. Interfaces + default methods mitigate.
- **Child/parent assignment:** `Parent p = new Child();` OK (upcast). `Child c = (Child) p;` OK only if runtime object is Child (ClassCastException otherwise).
- **Method param super type:** Yes — polymorphism: method(Super s) accepts Sub instances.
- **Access modifiers:** Private (class), package (default), protected (package + subclasses), public. Nested rules: inner class sees outer private.
- **default vs protected:** default = package only; protected = package + subclasses (even other packages).

### JVM / memory
- **Heap vs stack:** Stack: frames, locals, references; thread-private. Heap: objects, shared; GC subject.
- **GC / Young vs Old:** New objects in Young (Eden + Survivor); survivors promoted to Old. Marking via GC roots reachability; generational hypothesis: most objects die young.
- **String storage:** String pool (interned literals) in Java 8+ typically in heap; `new String("x")` always new heap object unless interned.
- **a,b,c string Q:** Literals may share pool → `a==b` often true. `new String("java")` → `a==c` false; `equals` true for same sequence.
- **How Stream is stored:** Not “stored” as data structure; pipeline + spliterator over source.
- **Primitives in collections:** Use wrappers (`Integer`); autoboxing. `int[]` → `IntStream.of(arr).boxed().collect(Collectors.toList())` or loop.

### Java core
- **Abstract class vs interface:** Abstract class: state, constructors, partial impl; single inheritance. Interface: contract, multiple inheritance of type; Java 8+ default/static methods.
- **Checked vs unchecked:** Checked must declare/handle (`IOException`); unchecked extend `RuntimeException` (`NPE`, `IllegalArgumentException`).
- **final:** Variable: assign-once; method: no override; class: no extend.
- **Immutable:** Object state cannot change after creation (`String`, `Integer`); `final` fields + no mutators + defensive copies for refs.
- **List vs Set:** List ordered, duplicates; Set unique, `equals/hashCode` for `HashSet`.
- **HashMap vs SortedMap:** HashMap O(1) avg unordered; `TreeMap`/`SortedMap` sorted keys O(log n).
- **ArrayList vs LinkedList:** ArrayList cache-friendly, indexed access; LinkedList node overhead, good middle insert (rare in practice).
- **Array vs ArrayList:** Fixed size vs growable; primitives vs boxed; generics.
- **Functional interface:** One abstract method (`@FunctionalInterface`); used by lambdas (`Supplier`, `Predicate`, `Function`).
- **Default method in interface:** Java 8+ `default void m(){}` for evolution of APIs.
- **equals/hashCode:** Contract: equal objects → same hashCode; override together for hash-based collections.
- **equals from which class?** `Object`; often overridden.
- **Can override equals and hashCode?** Yes; should together.

### Streams (specific Qs)
- **What are streams?** Lazy, functional-style sequence ops; not a data structure.
- **map vs flatMap:** map transforms element→one; flatMap element→stream then merged.
- **Intermediate vs terminal:** Intermediate return stream (lazy); terminal triggers execution (`collect`, `forEach`, `reduce`).
- **Short-circuit:** Ops that may not process whole stream (`findFirst`, `anyMatch`, `limit`).
- **Return types:** `map` → `Stream<R>`; `flatMap` → `Stream<R>`; `filter` → `Stream<T>`; terminal defines output (`Optional`, `List`, `long`, etc.).
- **Stream interface or class?** `interface Stream<T>`.
- **Use map to filter?** Wrong tool; `map` must return element per input — use `filter`. (You could map to `Optional` then `flatMap` — avoid.)
- **How stream uses functional interfaces:** `map`→`Function`, `filter`→`Predicate`, `forEach`→`Consumer`, supplier for `generate`.

### Multithreading
- **Multithreading:** Multiple threads of execution in one process.
- **Concurrency:** Dealing with multiple tasks progressing (may be threads, async, fibers); broader than “many threads.”
- **Two threads same structure:** Synchronize critical section, `ConcurrentHashMap`, locks (`ReentrantLock`), atomic classes, or actor/queue pattern; avoid locking whole app.
- **Singleton thread-safe:** Eager static instance; or DCL with `volatile`; or enum singleton (preferred).
- **Shallow vs deep copy:** Shallow: copy refs; deep: clone nested objects.
- **HashMap vs ConcurrentHashMap:** CHM concurrent reads + segmented/CAS writes; no `null` keys in CHM (older); HashMap not thread-safe.

### SQL
- **INNER vs LEFT:** Inner: match only; Left: all left rows + matched right or NULL.
- **WHERE vs HAVING:** WHERE filters rows before group; HAVING filters groups after `GROUP BY`.
- **CASE in WHERE?** Yes if boolean expression (e.g. compare column to computed CASE).
- **Indexing:** Faster lookup/sort/join; cost on write/storage; choose selective columns.
- **Employee count per dept:** `SELECT dept_id, COUNT(*) FROM employees GROUP BY dept_id;`
- **Update enum at runtime (DB)?** MySQL `ENUM` alter = DDL/migration; not “runtime” without schema change. App-level enums in Java are compile-time.
- **Stored proc external DB:** Linked server / federation / ETL / API — depends on vendor; mention security + transaction boundaries.
- **SQL vs NoSQL:** Relational ACID vs flexible schema, scale-out, CAP tradeoffs; MongoDB: documents, BSON, replica sets, sharding.
- **Chat app DB:** Often write-heavy + time series; consider Cassandra/Scylla/Dynamo + Redis for presence; or Postgres with partitioning; not one-size-fits-all.

### Design patterns
- **Factory:** Create objects without specifying exact class (`DocumentFactory.create("pdf")`).
- **Strategy:** Interchangeable algorithms (payment strategies) injected at runtime.
- **Where implemented:** Tie to real module (“our `PaymentProcessor` picked implementation by type”).

### Spring / Spring Boot
- **Spring vs Boot:** Spring = framework; Boot = opinionated auto-config, starters, embedded server, actuator.
- **@Controller vs @RestController:** `@RestController` = `@Controller` + `@ResponseBody` on methods.
- **DI / IoC:** Container creates/injects dependencies; you depend on abstractions.
- **Circular dependency:** Constructor injection fails; setter/field or refactor; `@Lazy`; best — break cycle by design.
- **Spring Security:** Filter chain → authentication (who) → authorization (what allowed); can use JWT in `OncePerRequestFilter` or resource server.
- **Exceptions in controller:** `@ControllerAdvice` + `@ExceptionHandler`; return `ProblemDetail` / consistent JSON.
- **Bean types:** singleton (default), prototype, request, session, application, websocket scopes.
- **Same return type beans:** Ambiguous injection → `@Primary`, `@Qualifier`, or `@Resource` name.
- **@Component vs @SpringBootApplication:** `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`; `@Component` stereotype for generic bean.
- **@Bean:** Method in `@Configuration` produces bean managed by context.
- **Circular dep (repeat):** Prefer splitting service or events.

### Security / API
- **AuthN vs AuthZ:** Identity vs permission.
- **POST vs PUT:** POST often create (non-idempotent); PUT replace at known URI (idempotent intent).
- **JWT vs OAuth token:** JWT often self-contained signed claims; “OAuth token” may be opaque reference checked at AS; OAuth is framework; JWT is format.
- **JWT signed & verified:** HMAC (shared secret) or RSA/ECDSA (private sign, public verify); verify signature + `exp`/`aud`/`iss`.
- **Gateway auth:** TLS termination, validate JWT/API key, route, rate limit, inject headers to downstream.
- **RBAC in Spring:** Roles in `UserDetails`; `@PreAuthorize("hasRole('ADMIN')")` + method security.

### JPA / Hibernate
- **Hibernate vs JPA:** JPA spec; Hibernate implementation.
- **Lazy vs Eager:** Lazy loads on access (watch N+1); eager loads with parent (can over-fetch).
- **@JoinColumn:** FK column owner side; `@JoinTable` for M2M with join table name + joinColumns/inverseJoinColumns.
- **ORM in Spring Boot:** Hibernate (default); entities ↔ tables via annotations/XML.
- **SessionFactory / Session:** SessionFactory heavy singleton; Session per unit of work (transaction); persistence context (1st level cache).
- **Cache levels:** L1 session; L2 (shared, entity cache — EHCache/Infinispan); query cache (separate, careful).

### Microservices
- **Loose coupling:** Services independent deploy/tech; contracts via APIs/events; avoid shared DB as integration.
- **API Gateway:** Single entry, routing, auth, rate limit, aggregation.
- **gRPC:** HTTP/2, protobuf, strong contracts, fast; browser needs gateway.
- **Kafka purpose:** Durable log, pub/sub, event streaming, replay, decoupling.
- **RabbitMQ:** Broker, queues, routing patterns, traditional messaging.
- **Alternatives:** NATS, Redis Streams, AWS SQS/SNS, Azure Service Bus/Event Hubs, Pulsar.
- **4 env CQRS URLs:** Config server / env vars / Key Vault / `application-{profile}.yml`; never hardcode; feature flags optional.
- **Resilience4J:** Circuit breaker, retry, rate limiter, bulkhead, time limiter.
- **Saga:** Distributed transaction via choreography/orchestration + compensations.
- **Service down:** Circuit breaker, fallbacks, retries with backoff, timeouts, idempotency, dead-letter queue, async messaging.
- **Rate limiting:** API GW, bucket in Redis, Resilience4J; per key/user/IP.

### DevOps / Azure / AWS
- **Deployment types:** Rolling, **blue-green** / **red-black** (same pattern, two names), **canary**, **shadow** (dark traffic), recreate.
- **Rolling:** Update instances in waves; old + new versions coexist briefly—needs backward-compatible schema/API.
- **Blue-green / red-black:** Two full stacks; flip traffic at LB/DNS; fast rollback; ~2× capacity during cutover; migrations must support idle stack if rolling back.
- **Canary:** Small % of real traffic to new version; ramp if metrics OK; limits blast radius; needs routing + observability.
- **Shadow:** Duplicate prod requests to new version; user still gets stable response; compare behavior—avoid double side effects (payments, emails).
- **Recreate:** Stop old, start new—downtime; dev/small tools only.
- **Flyway:** Versioned migrations on startup/CI; repeatable schema.
- **Logging/metrics:** Structured logs, correlation IDs, Micrometer + Prometheus/Grafana, Azure Monitor/App Insights.
- **CI/CD:** Build, test, scan (Sonar), artifact, deploy pipeline.
- **SonarCloud:** Static analysis, coverage, quality gates.
- **CORS:** Browser enforces; server sends `Access-Control-Allow-Origin` etc.; preflight for non-simple requests.

### Testing
- **Unit test:** Isolate unit; mock deps; fast.
- **Mockito:** Mock/stub collaborators; verify interactions.

### React / FE
- **Caching:** HTTP cache headers, SWR/React Query, service worker, memoization, CDN.
- **Cancel async:** `AbortController` with `fetch`; axios `CancelToken`; ignore stale responses with flag/ref; React Query cancellation.
- **?? vs ||:** `??` nullish only; `||` any falsy.
- **preventDefault on forms:** Stop full page reload; handle SPA submit.
- **Event bubbling:** Events propagate up DOM; `stopPropagation`.
- **Props drilling vs lifting:** Pass props many levels vs lift state to common ancestor / context.
- **this in arrow:** Lexical `this` from enclosing scope; not dynamic.
- **Arrow vs function:** `this` binding, hoisting, `arguments`.
- **Promise handling:** `.then/catch/finally`, `async/await`, `Promise.all/race/allSettled`.
- **Redundant awaits A→B→C:** One `async` function that awaits C only, or compose promises without nesting.
- **useEffect:** Side effects after paint; deps control re-run; cleanup for subscriptions.
- **Custom hooks:** Reuse stateful logic (`useX()`).
- **Redux dispatch/reducers:** Dispatch action → reducer pure function → new state.

### Algorithms / DS (quick)
- **HashMap internals:** `hashCode` → bucket index; equals for collision chain/tree (Java 8+).
- **TreeSet vs HashSet:** Sorted unique vs O(1) avg unordered unique.
- **Fibonacci / palindrome / anagram / swap:** Know O(n) string/array approaches; two pointers for palindrome.
- **Linked list reverse:** Iterative three pointers.

### Behavioral
- **SDLC:** Req → design → implement → test → deploy → maintain.
- **SOLID:** S single responsibility; O open/closed; L Liskov substitutability; I interface segregation; D dependency inversion. Example: `NotificationService` depends on `Notifier` interface, not `EmailSender` concrete.
- **ACID:** Atomicity, Consistency, Isolation, Durability.
- **Managing juniors:** Clear tasks, pair review, feedback, psychological safety, gradual ownership.
- **Profiling:** CPU/memory profilers (JProfiler, VisualVM, async-profiler), flame graphs, Micrometer metrics.

### Misc
- **Application architectures / component model:** Layered, hexagonal, microservices, CQRS — match to your project.
- **ERD:** Entities, relationships, cardinality, keys.
- **Session management:** Stateful server session vs JWT stateless; secure cookies, rotation.
- **Realtime dashboard backend:** Pre-aggregation, materialized views, caching, read replicas, WebSockets/SSE, stream processing (Kafka+Flink), avoid heavy queries per tick.

---

## 60-second Spring Security + JWT flow (memorize)

1. Client `POST /login` → credentials.  
2. Server validates → builds JWT (claims: sub, roles, exp) → signs.  
3. Client stores token (memory/httpOnly cookie).  
4. Each request: `Authorization: Bearer <jwt>`.  
5. Filter extracts token → verify signature + exp → build `Authentication` → `SecurityContext`.  
6. `AuthorizationManager` / method security checks roles → controller.

---

## If stuck: honest + adjacent experience

“I haven’t used X in production; I understand it’s used for [1-line]. In my stack I used [Y] for similar concern: [1 example]. I’d ramp by [docs + small POC].”

---

## Supplement — study sheet (synced with HTML `supplement-sheet`)

### OOP pillars
- **Encapsulation** — hide state, expose behavior; enforce invariants.
- **Abstraction** — what the type promises vs how it implements.
- **Inheritance** — is-a, single superclass in Java.
- **Polymorphism** — substitute subtypes; dynamic dispatch on instance methods.

### Multiple inheritance
- One **class** parent only (diamond problem); many **interfaces** OK.
- **Default methods** (Java 8+): conflict → implementing class must override.

### HashMap vs Hashtable vs ConcurrentHashMap
- **HashMap** — not thread-safe; one null key; default for single-threaded.
- **Hashtable** — legacy, synchronized methods; no null key/value; avoid new code.
- **ConcurrentHashMap** — concurrent reads/writes; no null keys/values; use for shared maps.

### Multithreading & deadlock
- **Deadlock** — circular wait + locks held; fix: lock ordering, tryLock/timeout, smaller critical sections.
- **synchronized** — monitor, reentrant; **ReentrantLock** — tryLock, fair option, interruptible.

### final / finally / finalize
- **final** — variable/method/class immutability or non-override.
- **finally** — always runs on exit from try (almost); prefer try-with-resources.
- **finalize** — deprecated; do not use for cleanup.

### default / static on interfaces
- Top-level interface is **not** “static” like a nested type; **fields** are `public static final`.
- **static** methods on interface (Java 8+) — call via `InterfaceName.method()`.
- **default** methods — instance methods with body on interface; can override in class.

### @Component vs @Bean
- **@Component** (+ stereotypes) — scan your class → bean.
- **@Bean** method in `@Configuration` — register third-party or custom-built instance.

### N+1
- 1 query for N parents + N lazy loads → use fetch join / entity graph / `@Query` + join fetch / batch size.

### Spring Data JPA
- `JpaRepository`, derived query names, `@Query`, paging, `Specification`, Hibernate as provider.

### First non-repeating char — `"SWISSIT"` → `W`
- Count with HashMap; **second loop over string** (not map iteration) for order.
- LinkedHashMap optional for insertion-order keys; CHM only if concurrent updates.

### SQL — repeating DOBs (`Emp(id, name, dob)`)
```sql
SELECT dob, COUNT(*) AS cnt
FROM Emp
GROUP BY dob
HAVING COUNT(*) > 1;
```

*(Full prose + code in `Systems-Limited-Interview-Prep.html` → section **Supplement — study sheet additions**.)*
