<Java Core>

***What's the differrence between Checked and Unchecked exceptions?***

[Checked] exception are enforced at compile time and must be caught in a try/catch block or declared with throws.

Usually represents recoverable conditions like IOException or SQLException.

[Unchecked] exceptions extend RuntimeException, is not checked at compile time, and usually repsents programming erros like NullPointerException or IndexOutOfBoundExceptionnós .

***What is Stream API?***

The ```Stream API``` was introduced in Java 8 to support functional-style operations on collections of data.

It allows processing sequences of elements in a declarative way, enabling operations such as filtering, mapping, reducing, and collecting.

***What's the difference between HEAP x STACK memory?***

The JVM memory is mainly divided into HEAD and Stack.

- [Heap] stores objects and is shared across all threads. It's managed by the Garbage Collector.
- [Stack], where each thread has it's own memory area. It stores method call frames, local variables, and object references in the Heap memory.
  
[Additionally], there is:

Metaspace, which stores class metadata and is allocated in native memory.

Program Counter (PC) Register, which keeps track of the current instruction being executed by each thread.

Native Method Stack, used for native (JNI) calls.

***What is Java Reflection?***

Java [Reflection]  is a mechanism that allows a program to inspect and manipulate it's own structure at runtime.

Using the java.lang.reflect package, we can:
- Inspect classes and interfaces
- Access fields and methods
- Invoke methods dynamically
- Access private members (by overriding access checks)
- Process annotations at runtime

[Reflection] is heavily used by frameworks such as [Spring] and [Hibernate] for dependency injection, and annotation processing.

For example, Spring scans the classpath, detects classes annotated with @Component, and dynamically creates and manages their instances inside the application context.

```java
    Class<?> clazz = Class.forName("com.example.User");

    Method method = clazz.getMethod("getName");
    Object instance = clazz.getDeclaredConstructor().newInstance();

    Object result = method.invoke(instance);
```

***What is Hash Collision?***

[Hash-Collision] occurs when two different objects generate the same ```hashCode()``` value.

```When hash codes are equal, Java must call equals() method``` to check real equality.

In hash-based data structure like HashMap or HashSet, the hash code is used to determine the bucket where an object will be stored. 

When two objects produce the same hash code, they are placed in the same bucket.

And when a collision happens, the objects in the same bucket are compared using equals() to determine if they are actually equal.

- Why must equal objects have the same hashCode?

    Equal objects must have the same hashCode because of the contract defined in the Java specification. If two objects are considered equal according to equals(), they must return the same hashCode to ensure correct behavior in hash-based collections like HashMap and HashSet.

***What's the difference between Array and List?***

[Array] has a fixed number of items. [Lists] are flexibles, we can add or remove whenever we need to.

***What's the difference between String, StringBuilder and StringBuffer?***

- [Strings] are immutable and are typically used to store values.
- [StringBuilder] and StringBuffer are used to create and manipulate text.

PS: [StringBuilder] is not thread-safe, while StringBuffer is thread-safe.

***What is a Functional Interface?***

.....................................

***What are Marker interfaces?***

[Marker-Interfaces] are interfaces with no methods. They are used to mark or classify a class so that the JVM or a framework can apply special behavior to it.

For example, we can create logic that applies specific behavior only to objects whose type implements a [Cacheble] interface. Another sample is [Serializable], that marks a class as eligible for serialization.

```Marker interfaces are conceptually simitar to annotations```. However, marker interfaces provide compile-time type safety, while annotations are more flexible and more commonly used in modern Java.

<Multithreading>

[Threads] are used to execute multiple tasks concurrently. Threads share the same Heap memory but have independent Stacks.

They help avoid blocking the main execution flow and allow parallelizing independent work. such as handling concurrent I/O operations or processing independent tasks in batch jobs.

In Java, [multithreading-components] can be implemented using:
- Thread
- Runnable
- ExecutorService
- CompletableFuture

```java
    //antigo
    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread running...");
        }
    }

    MyThread t = new MyThread();
    t.start();   // chama start(), NÃO run(). run executa na msm thread, start cria uma nova thread.

    //recomendado
    Runnable task = () -> {
        System.out.println("Thread running...");
    };

    Thread t = new Thread(task);
    t.start();

    //moderno
    ExecutorService executor = Executors.newFixedThreadPool(2);

    executor.submit(() -> {
        System.out.println("Running in pool");
    });


```

[Volatile-keyword] are variables visible to all running threads. When a variabe is declared as [volatile], changes made by one thread are immediately visible to other threads. It prevents threads from reading stale cached values.

```java
    //doesn't garantee atomicity
    private volatile int count;

    //Atomic classes - which rely on compare-and-swap operations
    AtomicInteger counter = new AtomicInteger(0);
    counter.incrementAndGet();

    //use synchronized blocks
    public synchronized void increment() {
        counter++;
    }
```

[Transient-keyword] is a field that will not be serialized when an object is converted into a byte stream. It's commonly used to exclude sensitive data from serialization.

```java
    private transient String password;
```

***What is the difference between run in Parallel x Background?***

[Concurrency-Background] = multiple tasks making progress
[Parallelism] = tasks running simultaneously on multiple cores

- [Parallel] execution means running multiple tasks at the same time, typically using multiple threads to improve performance. 

    ***Parallelism*** splits work into independent tasks to run concurrently.

    ***SAMPLE*** 
    
    When processing a large CSV file where each line can be validated independently, we can use ```parallel streams``` or an ```ExecutorService``` to distribute the workload across multiple THREADS, improving thoughput.

    Another example is feching User, Order and Payments data in parallel instead of sequentially. We can use [CompletableFuture] to reduce overall latency.

    Parallelism is useful for improving performance and reducing response time.

```java
    List<String> lines = Files.readAllLines(Path.of("file.csv"));

    lines.parallelStream()
        .map(this::validateLine)
        .forEach(this::processLine);
```

```java
    CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getUser(id));
    CompletableFuture<Order> orderFuture = CompletableFuture.supplyAsync(() -> orderService.getOrders(id));

    CompletableFuture.allOf(userFuture, orderFuture).join();
```
  
- [Background] process is a task executed outside the main executino flow, usually asynchonously, so it doesn't block the main thread. 

    ***Background*** execution focuseson avoiding blocking the main flow. 

    ***SAMPLE*** 

    For example, after a user registration, we can send a confirmaition email or publish an event a asynchonously. This allows the HTTP request to return immediatly while the task continues running in the background.

    Background execution improves responsiveness and user experience.

```java
    @Async //Spring annotation
    public void sendEmail(User user) {
        emailClient.send(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(...) {
        userService.createUser(...);
        emailService.sendEmail(user); // roda em background
        return ResponseEntity.ok().build();
    }
```
