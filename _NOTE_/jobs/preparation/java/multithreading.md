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
