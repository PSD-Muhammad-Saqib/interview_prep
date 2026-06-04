SPOKEN INTERVIEW ANSWERS — EXPANDED

=== BEHAVIORAL / EXPERIENCE ===

Q1: Backend Scheduler Components & Impact

"So in that project I was working as a backend developer
and my primary responsibility was around the server-side
architecture. The overall flow we had was fairly standard
for a distributed system — we had a REST controller layer
that was the entry point, accepting requests from the web
application. From there, instead of processing everything
synchronously, we pushed jobs into a message queue —
we were using RabbitMQ for that — which gave us the
ability to decouple the intake from the actual processing.

On the other side of that queue we had a worker pool —
essentially a set of consumers that would pick up jobs
and process them. And underneath all of that we had our
data and batch processing layers backed by the database.

Now my specific contribution within that flow was what
I'd call the driver-routing layer. The problem we had
was that different types of user requests needed to be
handled by different processing engines — and there was
no clean way to route them. So I designed the logic that
would inspect an incoming request, identify what kind of
processing it needed, and route it to the correct engine
or driver.

The impact of that was pretty direct — before this, jobs
were either failing silently or landing in the wrong
processor and erroring out. After implementing the
routing layer, we saw a significant reduction in failed
job retries, and the overall throughput of the system
improved because jobs weren't being wasted on wrong
handlers anymore."


Q2: Scalable & Maintainable Spring Boot APIs

"That's something I thought about quite deliberately on
that project. Let me break it into two parts because
scalability and maintainability are actually solved
differently.

For scalability, the most important decision we made
early on was to keep all our services completely
stateless. No session data was stored in the service
itself — everything was either in the database or passed
in the request. This meant we could spin up multiple
instances of any service behind a load balancer without
worrying about state synchronization. We also offloaded
any heavy processing to async job queues so we weren't
blocking HTTP threads — which kept our response times
low even under load. And on the database side we were
careful about connection pooling — Spring Boot uses
HikariCP by default which is excellent, and we tuned
the pool size based on our load patterns.

For maintainability, the biggest thing was strict
adherence to layered architecture. Every service had a
controller layer that only handled HTTP concerns, a
service layer that contained all business logic, and a
repository layer for data access. These never mixed
responsibilities. We also had centralized exception
handling using @ControllerAdvice so error responses
were consistent across every endpoint. And we versioned
all our APIs from day one — /api/v1/ — so we could
evolve them without breaking existing consumers.

And then on top of that we had solid test coverage —
unit tests on service logic using JUnit and Mockito,
and integration tests to verify the full request flow.
That gave us confidence to refactor without fear of
breaking things."


Q3: Mentoring Junior Developers

"Mentoring was actually something I found really
rewarding. When I joined the team there were a couple
of junior developers who were capable but were spending
a lot of time blocked — either waiting for answers or
submitting PRs that needed heavy rework.

The first thing I did was set up regular pair programming
sessions. Not just sitting next to them and telling them
what to write, but actually working through problems
together and thinking out loud so they could see how I
approached something — how I break down a problem, what
I check first, when I look at the docs versus when I
trust my understanding.

The second thing I changed was how I gave code review
feedback. Instead of just commenting 'this is wrong,
fix it', I started explaining the why behind every
piece of feedback. So if I flagged a design issue I'd
write something like 'this works but here's why it
might cause problems at scale, and here's an alternative
approach'. Over time they started internalizing those
patterns and applying them before submitting.

The results were noticeable fairly quickly. Within a
few weeks they were submitting PRs that needed much
lighter review. They were unblocking themselves more.
And there was a measurable improvement in code
consistency across the codebase because everyone was
now working from the same mental model of what good
code looked like on our team.

It also had a secondary benefit — senior developers
including myself had more time to focus on complex
problems because we weren't constantly fielding basic
questions."


=== OOP CONCEPTS ===

Q4: Interface vs Abstract Class

"This is one of those questions where I think the
textbook answer and the practical answer are slightly
different, so let me cover both.

Technically speaking, an interface in Java is a pure
contract. It defines what a class must do but has no
implementation — well, Java 8 introduced default
methods which blur that slightly, but the intent is
still a contract. And crucially, a class can implement
multiple interfaces, which gives you a form of multiple
inheritance of type.

An abstract class on the other hand can have both
abstract methods that subclasses must implement AND
concrete methods with actual implementation. It can
also have instance variables and constructors. But the
key constraint is single inheritance — you can only
extend one abstract class.

Now the practical question is when do I reach for one
versus the other. My rule of thumb is: if I'm defining
a capability that could apply across completely
unrelated classes, I use an interface. Think about
something like Serializable or Runnable — a Dog and a
NetworkConnection have nothing in common, but both
might need to be Serializable. That's an interface.

I use an abstract class when I have a clear family of
related types that share actual implementation — not
just a contract but real code I don't want to duplicate.
In Spring for example, if I have multiple service
classes that all need the same base logging or
validation logic, I'd put that in an abstract base
class and let them extend it.

The other thing worth mentioning is that interfaces
are better for testability because you can easily mock
them. So in a Spring application I almost always define
my service layer as an interface, even if there's only
one implementation, specifically so I can mock it in
unit tests."


Q5: Inheritance vs Composition

"This is one of the most important design decisions in
OOP and honestly one where I've seen a lot of codebases
go wrong by overusing inheritance.

The classic guidance is 'favor composition over
inheritance' — that comes from the Gang of Four design
patterns book — and I think it's right most of the time.

The distinction I use is the 'is-a' versus 'has-a'
test. If I can genuinely say 'A is a B' and that
relationship is stable and won't change, inheritance
might be appropriate. A Dog is an Animal. A
SavingsAccount is a BankAccount. These are real
hierarchical relationships.

But if the relationship is really 'A uses B' or 'A
has a B', composition is almost always better. And in
practice I find that most things that people model
with inheritance are actually 'has-a' relationships
in disguise.

The practical reason I prefer composition is coupling.
With inheritance, your subclass is tightly coupled to
the parent — if the parent class changes, every
subclass is potentially affected. I've seen this cause
subtle bugs that are really hard to track down. With
composition, the injected object is a dependency that
you can swap, mock in tests, or change independently.

A concrete example from my work: we had a
ReportService that initially extended a base
EmailService because reports needed to be emailed.
But when we needed to add Slack notifications, the
inheritance model broke down completely. We refactored
to inject a NotificationService interface instead —
and suddenly we could swap between email, Slack, or
anything else without touching ReportService at all.
That's the power of composition."

// ============================================
// BEFORE: Inheritance approach (the wrong way)
// ============================================

class EmailService {
    public void send(String message) {
        System.out.println("Sending email: " + message);
    }
}

// ReportService tightly coupled to EmailService
class ReportService extends EmailService {
    public void generateAndSend(String reportData) {
        String report = "Report: " + reportData;
        send(report); // inherited from EmailService
    }
}

// Problem: Now we need Slack too.
// We CAN'T extend both EmailService and SlackService
// Java doesn't allow multiple inheritance!
// The whole design breaks.


// ============================================
// AFTER: Composition approach (the right way)
// ============================================

// Step 1: Define a contract (interface)
interface NotificationService {
    void send(String message);
}

// Step 2: Each channel is its own implementation
class EmailNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("Sending email: " + message);
    }
}

class SlackNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("Sending Slack message: " + message);
    }
}

class SMSNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("Sending SMS: " + message);
    }
}

// Step 3: ReportService depends on the interface
// not on any concrete implementation
class ReportService {

    private final NotificationService notificationService;

    // Inject via constructor (Spring does this automatically)
    public ReportService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void generateAndSend(String reportData) {
        String report = "Report: " + reportData;
        notificationService.send(report);
        // ReportService doesn't care HOW it's sent
        // It just knows THAT it will be sent
    }
}

// ============================================
// MAIN: Swapping behavior without touching ReportService
// ============================================
public class Main {
    public static void main(String[] args) {

        // Use Email
        ReportService emailReport =
            new ReportService(new EmailNotificationService());
        emailReport.generateAndSend("Q1 Sales Data");
        // Output: Sending email: Report: Q1 Sales Data

        // Use Slack — zero changes to ReportService!
        ReportService slackReport =
            new ReportService(new SlackNotificationService());
        slackReport.generateAndSend("Q1 Sales Data");
        // Output: Sending Slack message: Report: Q1 Sales Data

        // Use SMS — still zero changes to ReportService!
        ReportService smsReport =
            new ReportService(new SMSNotificationService());
        smsReport.generateAndSend("Q1 Sales Data");
        // Output: Sending SMS: Report: Q1 Sales Data
    }
}


// ============================================
// SPRING BOOT VERSION (how it looks in real project)
// ============================================

@Service
public class ReportService {

    private final NotificationService notificationService;

    @Autowired
    public ReportService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void generateAndSend(String reportData) {
        String report = "Report: " + reportData;
        notificationService.send(report);
    }
}

// In application.properties or config, you decide
// which implementation Spring injects:

@Configuration
public class AppConfig {

    @Bean
    public NotificationService notificationService() {
        return new SlackNotificationService();
        // swap to EmailNotificationService() anytime
        // ReportService never changes
    }
}


Q6: Liskov Substitution Principle

"LSP is one of the SOLID principles and I think it's
one of the most practically important ones, even if
it sounds abstract when you first hear it.

The formal definition is: if S is a subtype of T,
then objects of type T in a program may be replaced
with objects of type S without altering the
correctness of that program. Essentially, a subclass
must be a drop-in replacement for its parent class.

In practice what this means is that when you override
a method, you have to honor the same contract that
the parent established — not just the method signature
but the behavioral expectations.

There are a few specific ways this gets violated.
First, strengthening preconditions — if the parent
method accepts any integer, your override shouldn't
suddenly reject negative numbers. That's adding a
restriction the caller doesn't know about. Second,
weakening postconditions — if the parent guarantees
a non-null return value, your override must also
return non-null. Third, throwing unexpected exceptions
— callers handle what the parent contract declares,
so surprising them with a new exception type breaks
substitutability.

The classic example everyone uses is Square extending
Rectangle. Mathematically a square is a rectangle,
so it seems like correct inheritance. But if you
override setWidth() on Square to also set the height
— because a square must have equal sides — you've
broken any code that does something like:

  Rectangle r = new Square();
  r.setWidth(5);
  r.setHeight(3);
  // expects area of 15, gets 9

The caller was treating it as a Rectangle but got
Square behavior. That's an LSP violation.

The way I avoid this in practice is to think about
whether my override is a true specialization of the
parent behavior, or whether it's actually a different
behavior that just happens to share a name. If it's
the latter, composition or a separate class hierarchy
is usually the right answer."

// ============================================
// LSP VIOLATION 1: Square extending Rectangle
// The classic example
// ============================================

class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getArea() {
        return width * height;
    }
}

// Seems logical — a square IS a rectangle, right?
class Square extends Rectangle {

    // Violation: overriding to enforce equal sides
    // breaks the Rectangle contract
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // side effect caller doesn't expect!
    }

    @Override
    public void setHeight(int height) {
        this.width = height; // side effect caller doesn't expect!
        this.height = height;
    }
}

class LSPViolationDemo {
    // This method works perfectly with Rectangle
    static void testArea(Rectangle r) {
        r.setWidth(5);
        r.setHeight(3);
        // Caller expects: 5 * 3 = 15
        System.out.println("Expected area: 15");
        System.out.println("Actual area:   " + r.getArea());
    }

    public static void main(String[] args) {
        System.out.println("=== With Rectangle ===");
        testArea(new Rectangle());
        // Expected area: 15
        // Actual area:   15 ✅

        System.out.println("=== With Square ===");
        testArea(new Square());
        // Expected area: 15
        // Actual area:   9 ❌ LSP VIOLATED!
        // setHeight(3) also set width to 3
        // so area = 3 * 3 = 9
    }
}


// ============================================
// LSP VIOLATION 2: Strengthening Preconditions
// Subclass rejects input the parent accepted
// ============================================

class PaymentProcessor {
    // Parent accepts ANY amount
    public void processPayment(int amount) {
        System.out.println("Processing payment: " + amount);
    }
}

class StrictPaymentProcessor extends PaymentProcessor {
    @Override
    public void processPayment(int amount) {
        // Violation: parent accepted any int
        // subclass suddenly rejects negatives
        if (amount < 0) {
            throw new IllegalArgumentException(
                "Amount cannot be negative!"
            );
        }
        System.out.println("Processing payment: " + amount);
    }
}

class PreconditionViolationDemo {
    static void makePayment(PaymentProcessor processor) {
        // Caller knows parent accepts any int
        // so passes -1 for a refund scenario
        processor.processPayment(-1);
    }

    public static void main(String[] args) {
        // Works fine with parent
        makePayment(new PaymentProcessor());    // ✅

        // Explodes with subclass — LSP VIOLATED
        makePayment(new StrictPaymentProcessor()); // ❌
    }
}


// ============================================
// LSP VIOLATION 3: Weakening Postconditions
// Subclass breaks the return guarantee
// ============================================

class UserRepository {
    // Parent guarantees: NEVER returns null
    // Returns a guest user if not found
    public User findById(int id) {
        User user = database.find(id);
        return user != null ? user : User.guestUser();
    }
}

class CachedUserRepository extends UserRepository {
    @Override
    public User findById(int id) {
        User cached = cache.get(id);
        // Violation: parent guaranteed non-null
        // subclass can return null — callers will NPE!
        return cached; // ❌ could be null
    }
}

class PostconditionViolationDemo {
    static void printUsername(UserRepository repo) {
        User user = repo.findById(999);
        // Caller trusts parent's guarantee of non-null
        System.out.println(user.getName()); // NPE if subclass!
    }

    public static void main(String[] args) {
        printUsername(new UserRepository());        // ✅
        printUsername(new CachedUserRepository());  // ❌ NullPointerException
    }
}


// ============================================
// LSP VIOLATION 4: Throwing Unexpected Exceptions
// ============================================

class FileReader {
    // Parent declares only IOException
    public String readFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}

class SecureFileReader extends FileReader {
    @Override
    public String readFile(String path)
            throws IOException, SecurityException { // ❌
        if (!isAuthorized(path)) {
            // Violation: caller only handles IOException
            // SecurityException is unexpected — will crash!
            throw new SecurityException("Access denied!");
        }
        return Files.readString(Path.of(path));
    }
}


// ============================================
// THE FIX: LSP Compliant Design
// ============================================

// Instead of inheritance, use proper abstractions

interface Shape {
    int getArea();
}

// Rectangle and Square are separate — no inheritance
class RectangleLSP implements Shape {
    private final int width;
    private final int height;

    public RectangleLSP(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getArea() {
        return width * height;
    }
}

class SquareLSP implements Shape {
    private final int side;

    public SquareLSP(int side) {
        this.side = side;
    }

    @Override
    public int getArea() {
        return side * side;
    }
}

class LSPCompliantDemo {
    // Works correctly with ANY Shape implementation
    static void printArea(Shape shape) {
        System.out.println("Area: " + shape.getArea());
    }

    public static void main(String[] args) {
        printArea(new RectangleLSP(5, 3)); // Area: 15 ✅
        printArea(new SquareLSP(4));       // Area: 16 ✅
        // No surprises — LSP honored!
    }
}


// ============================================
// QUICK REFERENCE: LSP Rules
// ============================================

/*
RULE 1 — Don't strengthen preconditions:
  Parent accepts: any int
  Subclass must:  also accept any int ✅
  Subclass must NOT: reject negatives ❌

RULE 2 — Don't weaken postconditions:
  Parent guarantees: non-null return
  Subclass must:     also return non-null ✅
  Subclass must NOT: return null ❌

RULE 3 — Don't throw unexpected exceptions:
  Parent declares:   throws IOException
  Subclass can:      throw IOException ✅
  Subclass must NOT: throw SecurityException ❌

RULE 4 — Honor behavioral contract:
  Parent behavior:   setWidth only changes width
  Subclass must:     do the same ✅
  Subclass must NOT: secretly change height too ❌

SIMPLE TEST:
  "Can I replace every instance of the parent
   with the subclass and have my program still
   work correctly?"
  YES → LSP honored ✅
  NO  → LSP violated ❌
*/

Q7/Q8/Q9: equals() and hashCode() Contract

"This is a topic I feel strongly about because
violations of this contract cause some of the nastiest
bugs I've seen — the kind where your code looks
completely correct but silently doesn't work.

Let me start with the contract itself. There are two
absolute rules:

Rule one: if two objects are equal according to
equals(), their hashCode() values must be identical.
No exceptions. This is the hard requirement.

Rule two: hashCode() must be consistent — as long as
the fields you use in equals() haven't changed, the
hashCode must return the same value every time you
call it.

Now why do these rules matter so much for HashMap
specifically? Because HashMap uses a two-step lookup.
First it calls hashCode() to find the right bucket.
Then within that bucket it calls equals() to find the
exact entry. If these two methods are out of sync,
the bucket lookup fails before equals even runs —
and your entry becomes unfindable.

The classic mistake is overriding equals() and
forgetting to override hashCode(). Say you have a
User class and you override equals() to compare by
email address. But you forget hashCode(). Now two
User objects with the same email will be considered
equal by equals(), but they'll have different hash
codes — because the default hashCode() is based on
object identity. So HashMap puts them in different
buckets, and when you try to look one up, you find
nothing.

As for the reverse — does equal hashCode mean equal
objects? No, and it doesn't need to. Two different
objects can have the same hashCode — that's just a
hash collision, and HashMap handles it by chaining
multiple entries in the same bucket and using equals()
to find the right one.

In practice I always use either Lombok's
@EqualsAndHashCode annotation or let the IDE generate
both together. The important thing is they're always
generated and updated as a pair, never independently."


=== COLLECTIONS & DATA STRUCTURES ===

Q10/Q11: ArrayList vs LinkedList

"These two get compared a lot and I think the key
is understanding what's actually happening in memory,
because that's what drives all the performance
differences.

ArrayList is backed by a contiguous array in memory.
When you call get(index), it's essentially just a
pointer arithmetic calculation — base address plus
offset. That's why it's O(1). It's instant regardless
of the size of the list. It also benefits from CPU
cache locality — because elements are stored next to
each other in memory, loading one element into cache
often brings its neighbors along too, which makes
sequential access very fast.

LinkedList is completely different. Each element is
a node object that contains the data and two pointers
— one to the previous node and one to the next. These
nodes can be scattered anywhere in memory. So when
you call get(5), you literally have to start from the
head node, follow the pointer to node 2, follow the
pointer to node 3, and so on until you reach index 5.
That's O(n) — it gets slower proportionally as the
list grows.

So why would you ever use LinkedList? The advantage
is insertions and deletions. If you already have a
reference to a node, inserting before or after it is
O(1) — you just update a couple of pointers. No
shifting of elements like ArrayList has to do.

In practice though, I almost always use ArrayList.
The reason is that even for insertions, the ArrayList
penalty is often acceptable because modern CPUs are
so fast at shifting contiguous memory. And the cache
locality advantage of ArrayList tends to outweigh
LinkedList's theoretical insertion advantage in
real benchmarks.

The cases where I'd actually reach for LinkedList
are: if I'm using it as a Queue or Deque — which it
implements natively — or if I have a very specific
access pattern with extremely frequent insertions
at the front or middle with no random access needed.
But that's rare."


Q12/Q13/Q14: HashMap Internals & Treeification

"HashMap is one of those data structures where
understanding the internals really pays off in
interviews and in debugging.

At its core, HashMap is an array of buckets. When
you put a key-value pair in, it calls hashCode() on
the key, does some additional bit manipulation to
spread the hash better, and uses the result to
determine which bucket index to put it in. For a
get(), it does the same calculation to find the
bucket, then uses equals() to find the exact entry
within that bucket.

Now collisions happen when two different keys map to
the same bucket. Before Java 8, HashMap handled this
purely with chaining — each bucket held a linked list
of entries, and you'd walk the list to find your key.
In the worst case if all keys collided into one bucket,
this degrades to O(n) — essentially a linear scan.

Java 8 introduced an optimization called treeification.
When a bucket's linked list grows beyond a threshold —
specifically 8 entries — and the overall table has at
least 64 buckets, HashMap converts that bucket's linked
list into a red-black tree. A balanced binary search
tree. This brings worst-case lookup within that bucket
from O(n) down to O(log n), which is significantly
better if you have many collisions.

The table capacity condition is important and often
missed. If the bucket hits 8 entries but the table
has fewer than 64 buckets, Java doesn't treeify —
it resizes the entire table instead. The logic is
that a small table probably just needs more buckets
to spread entries, not a tree structure. Treeification
only makes sense when the table is already large enough
that resizing alone wouldn't solve the collision problem.

For untreeification — going back from tree to linked
list — that happens when a bucket shrinks to 6 entries
or fewer. The gap between 6 and 8 is intentional,
it's a hysteresis buffer to prevent constant
back-and-forth conversion when entries hover around
the threshold."


=== CONCURRENCY ===

Q17: Mutable Keys in Multithreaded HashMap

"This is a really dangerous scenario and one I think
every Java developer needs to understand deeply.

Let's say you have a custom object as a HashMap key,
and its hashCode() is computed from one of its fields.
You insert it into the map — at that point the key
lands in a specific bucket based on its current hash.

Now if another thread mutates that field — the one
used in hashCode() — the object's hash code changes.
But the entry is still sitting in the old bucket.
The map has no idea the hash changed. So when you
later try to look up that key, HashMap computes the
new hash, looks in the new bucket, finds nothing,
and returns null — even though the entry is still
in the map, just in the wrong bucket. It's effectively
lost. You can't find it, you can't remove it, it just
sits there leaking memory.

That's the single-threaded version of the problem.
In a multithreaded context it gets worse because now
you also have a race condition. If one thread is
reading the key's fields to compute hashCode() at
the same moment another thread is writing those
fields, you get undefined behavior — corrupted hash
calculations, potentially infinite loops in older
Java versions during concurrent resize operations.

The solution is straightforward: use immutable objects
as HashMap keys. String and Integer are the canonical
examples — their fields never change after construction,
so their hashCode is always stable. If you must use a
mutable object as a key, you need to either synchronize
all access or use a ConcurrentHashMap with very careful
design."

import java.util.*;
import java.util.concurrent.*;

// ============================================
// THE MUTABLE KEY CLASS
// ============================================

class UserKey {
    private String email;  // mutable field used in equals/hashCode
    private String name;

    public UserKey(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email; // dangerous!
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserKey)) return false;
        UserKey other = (UserKey) o;
        return Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email); // based on mutable field!
    }

    @Override
    public String toString() {
        return "UserKey{email='" + email + "'}";
    }
}


// ============================================
// DEMO 1: Single-threaded mutation bug
// Key becomes unfindable after mutation
// ============================================

class SingleThreadedMutationBug {

    public static void main(String[] args) {
        Map<UserKey, String> map = new HashMap<>();

        UserKey key = new UserKey("saqib@gmail.com", "Saqib");

        // Step 1: Insert into map
        map.put(key, "Backend Developer");
        System.out.println("=== Single Threaded Mutation Bug ===");
        System.out.println("After insert:");
        System.out.println("  map.get(key) = "
            + map.get(key));         // ✅ "Backend Developer"
        System.out.println("  map.containsKey(key) = "
            + map.containsKey(key)); // ✅ true

        // Step 2: Mutate the key's field used in hashCode
        System.out.println("\nMutating key email...");
        key.setEmail("newsaqib@gmail.com");

        // Step 3: Try to find it now
        System.out.println("\nAfter mutation:");
        System.out.println("  map.get(key) = "
            + map.get(key));         // ❌ null — entry is LOST
        System.out.println("  map.containsKey(key) = "
            + map.containsKey(key)); // ❌ false

        // Step 4: But the entry IS still in the map!
        System.out.println("\nBut entry still exists in map:");
        System.out.println("  map.size() = "
            + map.size());           // still 1 — memory leak!

        // Prove it's still there by iterating
        for (Map.Entry<UserKey, String> entry : map.entrySet()) {
            System.out.println("  Found orphaned entry: "
                + entry.getKey() + " → " + entry.getValue());
        }

        /*
        OUTPUT:
        After insert:
          map.get(key) = Backend Developer       ✅
          map.containsKey(key) = true            ✅

        Mutating key email...

        After mutation:
          map.get(key) = null                    ❌
          map.containsKey(key) = false           ❌

        But entry still exists in map:
          map.size() = 1  (memory leak!)
          Found orphaned entry: UserKey{email='newsaqib@gmail.com'}
            → Backend Developer
        */
    }
}


// ============================================
// DEMO 2: Multithreaded mutation bug
// Race condition on top of the mutation problem
// ============================================

class MultiThreadedMutationBug {

    public static void main(String[] args)
            throws InterruptedException {

        Map<UserKey, String> map = new HashMap<>();
        UserKey key = new UserKey("saqib@gmail.com", "Saqib");
        map.put(key, "Backend Developer");

        System.out.println("=== Multi Threaded Mutation Bug ===");
        System.out.println("Starting concurrent access...\n");

        // Thread 1: repeatedly reads from the map
        Thread readerThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                String value = map.get(key);
                System.out.println("Reader: map.get(key) = "
                    + value);
                try { Thread.sleep(10); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ReaderThread");

        // Thread 2: mutates the key while reader is reading
        Thread mutatorThread = new Thread(() -> {
            try { Thread.sleep(15); } // let reader start first
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Mutator: changing email...");
            key.setEmail("hacked@gmail.com"); // race condition!
            System.out.println("Mutator: email changed!");
        }, "MutatorThread");

        readerThread.start();
        mutatorThread.start();
        readerThread.join();
        mutatorThread.join();

        System.out.println("\nFinal map.get(key) = "
            + map.get(key)); // ❌ null — key lost

        /*
        OUTPUT (non-deterministic):
        Reader: map.get(key) = Backend Developer
        Reader: map.get(key) = Backend Developer
        Mutator: changing email...
        Mutator: email changed!
        Reader: map.get(key) = null             ❌
        Reader: map.get(key) = null             ❌
        Reader: map.get(key) = null             ❌

        Final map.get(key) = null               ❌
        */
    }
}


// ============================================
// THE FIX 1: Immutable Key
// Fields final, no setters, hashCode always stable
// ============================================

final class ImmutableUserKey {
    private final String email; // final — can never change
    private final String name;

    public ImmutableUserKey(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // No setters — immutability enforced

    public String getEmail() { return email; }
    public String getName()  { return name;  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableUserKey)) return false;
        ImmutableUserKey other = (ImmutableUserKey) o;
        return Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email); // stable forever
    }

    @Override
    public String toString() {
        return "ImmutableUserKey{email='" + email + "'}";
    }
}

class ImmutableKeyDemo {
    public static void main(String[] args) {
        Map<ImmutableUserKey, String> map = new HashMap<>();

        ImmutableUserKey key =
            new ImmutableUserKey("saqib@gmail.com", "Saqib");

        map.put(key, "Backend Developer");

        System.out.println("=== Immutable Key Fix ===");
        System.out.println("map.get(key) = "
            + map.get(key)); // ✅ always works

        // key.setEmail("hacked@gmail.com"); 
        // ❌ WON'T COMPILE — no setter exists!
        // Immutability enforced at compile time

        System.out.println("map.get(key) = "
            + map.get(key)); // ✅ still works, always will
    }
}


// ============================================
// THE FIX 2: Use Java Record (Java 16+)
// Records are immutable by default — perfect for keys
// ============================================

record UserKeyRecord(String email, String name) {
    // Records auto-generate:
    // - final fields
    // - constructor
    // - equals() based on all fields
    // - hashCode() based on all fields
    // - toString()
    // No setters generated — immutable by design
}

class RecordKeyDemo {
    public static void main(String[] args) {
        Map<UserKeyRecord, String> map = new HashMap<>();

        UserKeyRecord key =
            new UserKeyRecord("saqib@gmail.com", "Saqib");

        map.put(key, "Backend Developer");

        System.out.println("=== Record Key Fix ===");
        System.out.println("map.get(key) = "
            + map.get(key)); // ✅ always reliable

        // Records are immutable — no mutation possible
        // This is the cleanest solution in modern Java
    }
}


// ============================================
// THE FIX 3: ConcurrentHashMap for thread safety
// Still use immutable keys, but now map is thread-safe
// ============================================

class ConcurrentMapDemo {
    public static void main(String[] args)
            throws InterruptedException {

        // ConcurrentHashMap for thread-safe access
        ConcurrentHashMap<ImmutableUserKey, String> map =
            new ConcurrentHashMap<>();

        ImmutableUserKey key =
            new ImmutableUserKey("saqib@gmail.com", "Saqib");

        map.put(key, "Backend Developer");

        // Atomic operations — no external synchronization needed
        map.putIfAbsent(key, "Should not replace");
        map.computeIfAbsent(
            new ImmutableUserKey("other@gmail.com", "Other"),
            k -> "New Developer"
        );

        System.out.println("=== ConcurrentHashMap Fix ===");
        map.forEach((k, v) ->
            System.out.println(k + " → " + v)
        );
    }
}


// ============================================
// QUICK REFERENCE SUMMARY
// ============================================

/*
THE PROBLEM:
  1. Key inserted → lands in bucket based on hashCode
  2. Key field mutated → hashCode changes
  3. Lookup computes NEW hash → looks in WRONG bucket
  4. Entry not found → returns null
  5. Entry still in old bucket → memory leak
  6. In multithreaded: race condition on field read/write
     → corrupted hash, undefined behavior

THE FIXES:
  Fix 1: Make key class immutable
         → final fields, no setters
         → hashCode can never change

  Fix 2: Use Java Record (Java 16+)
         → immutable by design
         → equals/hashCode auto-generated correctly

  Fix 3: Use ConcurrentHashMap
         → thread-safe reads and writes
         → still MUST use immutable keys!
         → ConcurrentHashMap fixes thread safety
            but NOT the mutation problem

GOLDEN RULE:
  "Always use immutable objects as HashMap keys.
   String and Integer are the gold standard —
   their hashCode is computed once and cached."
*/

Q18: ConcurrentHashMap vs synchronizedMap

"This comes down to how granular the locking is,
and that granularity has a massive impact on
throughput under concurrency.

Collections.synchronizedMap() is essentially a wrapper
that puts a single synchronized block around every
method of the map. So every get(), every put(), every
containsKey() — all of them acquire the same lock.
This means at any given moment only one thread can
do anything with the map. Reads block writes, writes
block reads, reads block reads. It's completely
serialized access. Thread-safe, yes, but it becomes
a bottleneck as soon as you have more than a few
threads.

ConcurrentHashMap takes a much more sophisticated
approach. In Java 8 and later it uses a combination
of CAS operations and synchronized blocks at the
individual bucket level. Reads are almost entirely
lock-free — they use volatile reads and don't acquire
any lock at all in most cases. Writes only lock the
specific bucket being modified, not the entire map.
So two threads writing to different buckets can
proceed completely in parallel.

The practical difference in a high-traffic service
can be dramatic. I've seen benchmarks where
ConcurrentHashMap outperforms synchronizedMap by
an order of magnitude under concurrent load.

For production code I always default to
ConcurrentHashMap when I need a thread-safe map.
The only scenario where I might reach for
synchronizedMap is if I need to perform a compound
operation atomically — like check-then-act across
the whole map — and I want to hold the lock myself
externally. But even then there are usually better
patterns using ConcurrentHashMap's atomic methods
like computeIfAbsent() or putIfAbsent()."


Q19/Q20/Q21: volatile, AtomicBoolean, Visibility vs Atomicity

"These three concepts are closely related so let me
connect them together.

The Java Memory Model defines how threads interact
with memory. The problem it's solving is that modern
CPUs have multiple levels of cache, and each thread
may be working with its own cached copy of a variable
rather than the value in main memory. So thread A
writes true to a boolean flag, but thread B is still
reading false from its CPU cache — it never sees the
update. This is the visibility problem.

volatile solves exactly this. When you declare a
variable volatile, you're telling the JVM: every
write to this variable must be flushed to main memory
immediately, and every read must go directly to main
memory rather than a CPU cache. So the update becomes
visible across all threads instantly.

But here's the critical limitation of volatile — it
only guarantees visibility, not atomicity. Atomicity
means an operation completes as one single
uninterruptible unit that no other thread can observe
in a halfway state.

The classic example is counter++. This looks like one
operation but it's actually three: read the current
value from memory, add one to it, write the result
back. These three steps are not atomic. Two threads
can both execute step one simultaneously, both read
the same value of say 5, both independently compute
6, and both write 6 back. The result is 6 instead of
7 — you've lost an increment. And this happens even
if counter is declared volatile, because volatile
can't prevent another thread from interleaving
between those three steps.

For a simple stop flag where one thread writes true
and other threads just read it, volatile is perfectly
sufficient — it's a single write and single reads,
no compound operation.

But when you need read-modify-write atomicity, you
need AtomicInteger or AtomicBoolean. These use
compare-and-swap under the hood — a CPU-level
instruction that reads a value, compares it to an
expected value, and only writes the new value if
the comparison succeeds, all as one uninterruptible
hardware operation. So incrementAndGet() on an
AtomicInteger is genuinely atomic — no interleaving
possible."


=== CODING EXERCISE ===

Q22: Stadium Ticket Sales — Full Thought Process

"Let me walk through how I'd approach this from scratch.

UNDERSTANDING THE PROBLEM:
First I'd make sure I fully understand what's being
asked. We have multiple gates, each with a queue of
people waiting. Each person in the queue wants some
number of tickets — that's what the integer represents.
K is a cap on how many people each gate can serve in
total — not tickets, people. And we process them in
round-robin order — one person from gate 0, one from
gate 1, and so on, cycling back until everyone's been
served or all gates hit their K limit.

The output is the order in which people were served,
represented as gate index and their ticket count.

IDENTIFYING THE CORE CHALLENGE:
The challenge is managing state across multiple queues
simultaneously while cycling through them. I need to
track two things per gate: where I am in its queue,
and how many people I've served from it.

THINKING ABOUT DATA STRUCTURES:
My first instinct is simple arrays — one for current
index per gate, one for served count per gate. This
is O(n) space and gives O(1) access per gate per
round. I don't need anything more complex than that.

I could use a HashMap mapping gate index to its state,
which would be useful if gate IDs were non-sequential
or dynamic. But for this problem where gates are
indexed 0 to n-1, arrays are cleaner and faster.

EDGE CASES I'D CONSIDER:
What if a gate starts empty? My index check handles
that — index 0 is already at or beyond length 0.
What if a gate has fewer requests than K? It just
exhausts naturally and gets skipped in subsequent
rounds. What if K is larger than any queue? Same
thing — gates finish early and are skipped.

TERMINATION CONDITION:
The loop ends when we complete a full pass across
all gates without serving anyone. I track this with
an anyActive flag that starts false each round and
gets set true whenever we serve someone.

SOLUTION WALKTHROUGH WITH EXAMPLE:
gates = [[2,3,5], [4,1], [6,7]], K = 2

Round 1:
Gate 0: count=0 < K=2, index=0 < 3 → serve (0,2),
        count becomes 1, index becomes 1
Gate 1: count=0 < K=2, index=0 < 2 → serve (1,4),
        count becomes 1, index becomes 1
Gate 2: count=0 < K=2, index=0 < 2 → serve (2,6),
        count becomes 1, index becomes 1
anyActive = true, continue

Round 2:
Gate 0: count=1 < K=2, index=1 < 3 → serve (0,3),
        count becomes 2, index becomes 2
Gate 1: count=1 < K=2, index=1 < 2 → serve (1,1),
        count becomes 2, index becomes 2
Gate 2: count=1 < K=2, index=1 < 2 → serve (2,7),
        count becomes 2, index becomes 2
anyActive = true, continue

Round 3:
Gate 0: count=2 >= K=2 → SKIP
Gate 1: count=2 >= K=2 → SKIP
Gate 2: count=2 >= K=2 → SKIP
anyActive stays false → EXIT LOOP

Final output: [(0,2),(1,4),(2,6),(0,3),(1,1),(2,7)] ✓

COMPLEXITY ANALYSIS:
Time: O(G * K) where G is number of gates — in the
worst case we do K rounds, each touching all G gates.
Space: O(G) for tracking arrays, O(total served) for
the result list.

CODE QUALITY NOTE:
For production code I'd use a record type for the
output instead of int[] — it's self-documenting and
type-safe:

record GateRequest(int gateIndex, int tickets) {}

This makes the return type meaningful and the code
easier to understand and maintain."


================================================================
SPRING CORE CONCEPTS — SPOKEN INTERVIEW ANSWERS
================================================================


================================================================
1. SPRING BEANS
================================================================

CONCEPT EXPLANATION (spoken):

"So a Spring Bean is essentially any object that is
managed by the Spring IoC container. The key word
there is 'managed' — it doesn't just mean Spring
created it, it means Spring owns its entire lifecycle:
creation, configuration, wiring with other objects,
and destruction.

The way I think about it is: in a traditional Java
application, if ServiceA needs ServiceB, ServiceA
is responsible for creating ServiceB — it does
'new ServiceB()' somewhere. That creates tight
coupling. Spring flips this around — you declare
your classes as beans, tell Spring about their
dependencies, and Spring handles all the wiring.
You just ask for what you need, Spring provides it.

In a Spring Boot application you declare beans in
a few ways. The most common is @Component and its
specializations — @Service for business logic,
@Repository for data access, @Controller or
@RestController for web layer. These are detected
automatically through component scanning. The other
way is @Bean inside a @Configuration class, which
gives you more control over how the object is
constructed — useful for third-party classes you
can't annotate directly.

Now one thing interviewers love to ask about is
bean scope. By default every Spring bean is a
singleton — one instance shared across the entire
application context. That's appropriate for
stateless services. But you can change it — prototype
scope creates a new instance every time the bean is
requested, request scope creates one per HTTP request,
session scope one per user session. In practice I
almost always use singleton because my service classes
are stateless by design."


COMMON INTERVIEW QUESTIONS ON BEANS:

Q: What is a Spring Bean?
"A Spring Bean is any object whose lifecycle —
creation, dependency injection, and destruction —
is managed by the Spring IoC container. You declare
a class as a bean using annotations like @Component,
@Service, @Repository, or @Bean in a configuration
class. Spring then instantiates it, injects its
dependencies, and makes it available throughout
the application."

Q: What are Bean scopes?
"There are five main scopes. Singleton is the default
— one shared instance per application context, which
is appropriate for stateless services. Prototype
creates a fresh instance every time the bean is
requested — use this for stateful objects. Request
creates one instance per HTTP request, session one
per user session, and application one per servlet
context. In my day-to-day work I almost exclusively
use singleton because keeping services stateless is
a core design principle in microservices."

Q: What is the Bean lifecycle?
"The lifecycle goes through several phases. First
Spring instantiates the bean using its constructor.
Then it injects all the dependencies. Then if the
bean implements InitializingBean or has a method
annotated with @PostConstruct, that runs — good for
any setup logic. The bean is then ready and lives in
the container. When the context shuts down, @PreDestroy
runs for any cleanup, like closing connections or
releasing resources. In practice I use @PostConstruct
for things like loading configuration on startup and
@PreDestroy for graceful shutdown cleanup."

Q: Difference between @Component, @Service,
   @Repository, @Controller?
"They're all specializations of @Component — at the
technical level they're equivalent in terms of bean
registration. The difference is semantic and
functional. @Service signals business logic layer.
@Repository signals data access layer and has the
added benefit of Spring translating persistence
exceptions into Spring's DataAccessException
hierarchy. @Controller marks a web layer component.
I always use the specific annotation rather than
generic @Component because it makes the architecture
immediately clear to anyone reading the code."


================================================================
2. INVERSION OF CONTROL (IoC)
================================================================

CONCEPT EXPLANATION (spoken):

"IoC is a design principle, not a Spring-specific
thing, though Spring is probably its most famous
implementation. The core idea is inverting who is
responsible for creating and managing dependencies.

In traditional programming, your code is in control.
If ClassA needs ClassB, ClassA creates ClassB —
'new ClassB()'. ClassA controls the flow. With IoC,
you invert that — you give up control of object
creation to a container or framework. You declare
what you need, and the container figures out how
to provide it. The container is in control, not
your code.

The reason this matters so much is testability and
flexibility. If ClassA creates its own ClassB
internally, you can never replace ClassB with a
mock in a test — the dependency is hardcoded. With
IoC, the container injects ClassB from outside, so
in a test you can inject a mock instead. Your code
becomes decoupled from its dependencies.

The Spring IoC container is represented by two
main interfaces — BeanFactory, which is the basic
container, and ApplicationContext which extends it
and adds enterprise features like event publishing,
internationalization, and AOP support. In practice
you always use ApplicationContext — BeanFactory is
rarely used directly anymore.

I always explain IoC with a real analogy: think of
a restaurant. In the traditional model, if you want
water you go to the kitchen and get it yourself.
With IoC, you sit down, declare that you need water,
and the waiter brings it to you. You declared the
dependency, the container fulfilled it. You never
left your seat."


COMMON INTERVIEW QUESTIONS ON IoC:

Q: What is Inversion of Control?
"IoC is a principle where the control of creating
and managing object dependencies is transferred from
the application code to a container or framework.
Instead of objects creating their own dependencies
with 'new', they declare what they need and the
container provides it. The benefit is loose coupling
— your classes don't depend on concrete
implementations, making them easier to test, maintain,
and swap out."

Q: What is the Spring IoC container?
"The Spring IoC container is responsible for
instantiating, configuring, and assembling beans.
It reads configuration metadata — either annotations,
XML, or Java config classes — to know what beans to
create and how to wire them together. The container
is represented by the ApplicationContext interface.
When your Spring Boot application starts, it
bootstraps an ApplicationContext, registers all
your beans, injects all dependencies, and then
your application is ready to serve requests."

Q: What is the difference between BeanFactory
   and ApplicationContext?
"BeanFactory is the basic IoC container — it provides
bean instantiation and dependency injection but
nothing else. ApplicationContext extends BeanFactory
and adds enterprise features: event publishing,
AOP support, internationalization, and eager
initialization of singleton beans on startup.
In practice you always use ApplicationContext —
BeanFactory is considered a lower-level API and
is rarely used directly in modern Spring applications."


================================================================
3. DEPENDENCY INJECTION (DI)
================================================================

CONCEPT EXPLANATION (spoken):

"Dependency Injection is the mechanism through which
IoC is implemented. If IoC is the principle — give
up control of dependency creation — then DI is how
Spring actually does it. It literally injects the
dependencies into your class from outside.

There are three types of injection in Spring.
Constructor injection, setter injection, and field
injection. And I have strong opinions about which
to use.

Constructor injection is where dependencies are
passed through the constructor. This is my default
choice and Spring's recommended approach since
Spring 4. The reason is that it makes dependencies
explicit — you can see exactly what a class needs
just by looking at its constructor. It also enables
immutability — you can make fields final. And it
makes testing trivial — you just pass mocks directly
into the constructor without any Spring context.

Field injection uses @Autowired directly on a field.
It looks clean on the surface but it's actually
problematic. You can't make fields final, you can't
easily test without a Spring context, and it hides
dependencies — you can't tell what a class needs
without reading through all its fields. I avoid
this in production code.

Setter injection is the middle ground — useful when
a dependency is optional or when you need to change
it after construction. But for mandatory dependencies
constructor injection is always cleaner.

One more thing worth mentioning is @Qualifier. When
you have multiple beans of the same type, Spring
doesn't know which one to inject. @Qualifier lets
you specify exactly which implementation you want
by name. I've used this in projects where we had
multiple datasource configurations and needed to
be explicit about which one each repository used."


COMMON INTERVIEW QUESTIONS ON DI:

Q: What are the types of Dependency Injection
   in Spring?
"There are three types. Constructor injection passes
dependencies through the constructor — this is the
recommended approach because dependencies are
explicit, fields can be final, and testing is easy.
Setter injection uses @Autowired on setter methods —
useful for optional dependencies. Field injection
uses @Autowired directly on fields — it looks clean
but I avoid it because fields can't be final, testing
requires a Spring context, and dependencies are
hidden. Constructor injection is the right default
for any mandatory dependency."

Q: What is @Autowired and how does it work?
"@Autowired tells Spring to inject a dependency
automatically. When Spring creates a bean, it looks
at fields, constructors, or setters annotated with
@Autowired, finds a matching bean in the context
by type, and injects it. If there are multiple beans
of the same type, Spring throws a
NoUniqueBeanDefinitionException — you resolve this
with @Qualifier to specify which bean by name, or
@Primary to mark one bean as the default choice."

Q: Constructor vs Field Injection — which do
   you prefer and why?
"Constructor injection, always, for mandatory
dependencies. The reasons are: first, fields can
be declared final which enforces immutability —
dependencies can never be accidentally replaced.
Second, the class is completely testable without
a Spring context — you just pass mocks into the
constructor directly. Third, if a class has too
many constructor parameters it's a code smell
that the class is doing too much — constructor
injection makes that visible immediately. Field
injection hides this problem. In Spring Boot with
Lombok I use @RequiredArgsConstructor which
generates the constructor automatically for all
final fields — clean and no boilerplate."

Q: What is @Qualifier?
"@Qualifier is used when you have multiple beans
of the same type and Spring doesn't know which one
to inject. You annotate the injection point with
@Qualifier and the bean name. For example if I have
two DataSource beans — primaryDataSource and
secondaryDataSource — I annotate the one I want
with @Qualifier('primaryDataSource'). In practice
I've used this in projects with multiple database
connections where different repositories needed
to target different datasources."

Q: What is circular dependency and how do
   you resolve it?
"A circular dependency is when Bean A depends on
Bean B and Bean B depends on Bean A — Spring can't
create either one first. With constructor injection
Spring detects this at startup and throws a
BeanCurrentlyInCreationException — which is actually
helpful because it fails fast. The fix is usually
a design problem — if two classes depend on each
other, they're probably too tightly coupled and
need to be refactored. If refactoring isn't
immediately possible, @Lazy on one of the injections
defers its creation and breaks the cycle. But I
treat circular dependencies as a design smell and
fix the root cause rather than masking it."


================================================================
4. SPRING SECURITY
================================================================

CONCEPT EXPLANATION (spoken):

"Spring Security is a powerful and highly customizable
authentication and authorization framework for Spring
applications. I'd say it's one of those frameworks
that looks intimidating at first because of the
filter chain concept, but once you understand the
core flow it becomes very logical.

The fundamental model is: every HTTP request passes
through a chain of security filters before reaching
your controller. Spring Security inserts its own
FilterChain into the servlet filter chain. These
filters handle things like reading the JWT token
from the header, authenticating the user, checking
permissions, handling CORS, CSRF protection, and
so on.

The two core concepts are authentication and
authorization. Authentication answers 'who are you?'
— verifying identity, usually with a username and
password or a token. Authorization answers 'what
are you allowed to do?' — checking permissions
after identity is confirmed.

For authentication, the key class is
AuthenticationManager, which delegates to one or
more AuthenticationProviders. The most common one
is DaoAuthenticationProvider, which loads user
details from a database via UserDetailsService,
then verifies the password using a PasswordEncoder
— always BCrypt in modern applications.

For stateless REST APIs, which is what I work with
most, we use JWT — JSON Web Tokens. The flow is:
user logs in with credentials, server validates them,
generates a signed JWT, and returns it. Client stores
the token and sends it in the Authorization header
with every subsequent request. The server validates
the token signature on each request — no session
state needed on the server side, which is perfect
for microservices.

For authorization, Spring Security gives you method
level security with @PreAuthorize and URL level
security in the SecurityFilterChain configuration.
I prefer @PreAuthorize because it keeps authorization
logic close to the business logic rather than spread
across a configuration file."


COMMON INTERVIEW QUESTIONS ON SPRING SECURITY:

Q: How does Spring Security work internally?
"Spring Security works through a chain of servlet
filters called the SecurityFilterChain. Every
incoming HTTP request passes through these filters
before reaching the controller. Key filters include
UsernamePasswordAuthenticationFilter for form login,
BearerTokenAuthenticationFilter for JWT, and
ExceptionTranslationFilter for converting security
exceptions into HTTP responses like 401 or 403.
The filter chain is highly configurable — in modern
Spring Security you define it by declaring a
SecurityFilterChain bean with a HttpSecurity builder."

Q: What is the difference between Authentication
   and Authorization?
"Authentication is verifying identity — confirming
you are who you claim to be, typically via username
and password or a token. Authorization is verifying
permissions — confirming that the authenticated
user is allowed to perform a specific action or
access a specific resource. Authentication always
happens first. In Spring Security, authentication
is handled by AuthenticationManager and its
providers. Authorization is handled by
AccessDecisionManager or the newer
AuthorizationManager in Spring Security 5.x,
checking granted authorities against required roles."

Q: How do you implement JWT authentication
   in Spring Boot?
"The flow has a few key steps. First, you create
a login endpoint that accepts credentials, validates
them using AuthenticationManager, and if valid
generates a signed JWT using a library like jjwt.
The token contains the username and roles as claims
and is signed with a secret key.

Second, you create a filter — typically extending
OncePerRequestFilter — that intercepts every request,
extracts the token from the Authorization header,
validates the signature and expiry, and if valid
sets the authentication in the SecurityContextHolder.

Third, you configure the SecurityFilterChain to be
stateless — sessionCreationPolicy STATELESS — and
add your JWT filter before the standard authentication
filter.

From that point every request is authenticated by
the token alone — no session on the server, which
is exactly what you want for a microservices
architecture."

Q: What is the SecurityContextHolder?
"SecurityContextHolder is a thread-local storage
that holds the security context for the current
request — specifically the Authentication object
representing the currently logged-in user. Once
your JWT filter validates a token and sets the
Authentication in the SecurityContextHolder, the
rest of the request processing — including your
controllers and services — can access the current
user via SecurityContextHolder.getContext()
.getAuthentication(). Spring Security uses this
to enforce authorization decisions throughout the
request. The context is cleared at the end of each
request, which is why stateless JWT works — there's
nothing to persist between requests."

Q: What is the difference between @Secured,
   @RolesAllowed and @PreAuthorize?
"All three do method-level authorization but with
different capabilities. @Secured and @RolesAllowed
are simpler — they check if the user has a specific
role, that's it. @PreAuthorize is more powerful
because it uses Spring Expression Language, so you
can write complex conditions like checking the
user's ID matches a path variable, or combining
multiple role checks with AND/OR logic. I always
use @PreAuthorize in production because the
flexibility is essential — real applications
rarely have authorization rules as simple as just
'has this role'."

Q: How do you handle password encoding?
"Always use BCryptPasswordEncoder — never store
plain text or use MD5/SHA which are reversible or
fast to brute force. BCrypt is a slow hashing
algorithm by design — the cost factor makes brute
force attacks computationally expensive. In Spring
Security you declare a PasswordEncoder bean using
BCryptPasswordEncoder, then use it to encode
passwords on registration and to verify passwords
on login via matches(). Spring's
DaoAuthenticationProvider handles the verification
automatically — you just need to inject the
PasswordEncoder bean and configure UserDetailsService."


================================================================
QUICK REFERENCE — WHICH ANNOTATION DOES WHAT
================================================================

/*
BEAN DECLARATION:
@Component      → generic bean, auto-detected
@Service        → business logic layer bean
@Repository     → data access layer bean +
                  exception translation
@Controller     → web layer bean (returns views)
@RestController → web layer bean (returns JSON)
@Bean           → manual bean in @Configuration class

DEPENDENCY INJECTION:
@Autowired      → inject by type
@Qualifier      → specify which bean by name
@Primary        → default bean when multiple exist
@Lazy           → defer bean creation
@Value          → inject property value

BEAN LIFECYCLE:
@PostConstruct  → run after bean is initialized
@PreDestroy     → run before bean is destroyed

SECURITY:
@PreAuthorize   → method security with SpEL
@Secured        → method security with roles only
@EnableWebSecurity          → enable security config
@EnableMethodSecurity       → enable method security

SCOPES:
@Scope("singleton")  → default, one instance
@Scope("prototype")  → new instance each time
@RequestScope        → one per HTTP request
@SessionScope        → one per user session
*/