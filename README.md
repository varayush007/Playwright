# SDET Machine Coding Practice (Java 17 + TestNG + Mockito)

Source under `src/main/java/com/prep/sdet`. Write your own tests under `src/test/java/com/prep/sdet`.
`src/test` ships with 3 starter examples (parametrized, Mockito, fake clock) plus a smoke test.

Full reference tests for every snippet are in `src/solutions/java/com/prep/sdet/solutions`
(package `com.prep.sdet.solutions`, so they never clash with your own test class names).
Try each snippet cold first, then compare.

Run your tests: `mvn test`
Run your tests plus all reference tests: `mvn test -Psolutions`
One class: `mvn -Dtest=OrderServiceTest test`  (add `-Psolutions` for a reference class)

With `-Psolutions`, 15 or so test cases fail on purpose. They expose the planted bugs (marked `// FAILS` in the code),
and `HitCounterTest` fails only sometimes because it is a race. Failures anywhere else mean something is wrong, so investigate.

Reference test files: ShippingCalculator, OrderService and TokenBucketRateLimiter are in `src/test`;
TextUtils, CollectionAlgos, BankAccount, LruCache, TtlCache, OrderStatus (state machine), CsvLineParser,
RomanNumerals, MoneyMath, RetryExecutor, Paginator, EventBus, CodeGenerator, ConfigLoader, HitCounter, Money
and Playlist are in `src/solutions`.

| # | Archetype | File(s) | Key techniques |
|---|---|---|---|
| 1 | Branches / boundaries | ShippingCalculator | DataProvider, boundary values, expectedExceptions |
| 2 | Strings | TextUtils | null/empty/unicode/special chars |
| 3 | Collections | CollectionAlgos | empty, duplicates, input not mutated |
| 4 | Stateful | BankAccount, LruCache | state transitions, invariants, eviction order |
| 5 | Time | TokenBucketRateLimiter, TtlCache | MutableClock (test util), boundary at exact expiry |
| 6 | Dependencies | OrderService (+ OrderRepository, PaymentGateway, Notifier) | Mockito: stub, verify, never, InOrder, ArgumentCaptor, doThrow |
| 7 | State machine | OrderStatus, Order | all valid and invalid transitions, terminal states |
| 8 | Parser | CsvLineParser, RomanNumerals | malformed input, round-trip (toInt(toRoman(n)) == n) |
| 9 | Numeric/money | MoneyMath | delta in assertEquals, rounding, divide by zero |
| 10 | Retry | RetryExecutor (+ Sleeper) | mock that fails N times, verify sleep delays, exhaustion |
| 11 | Pagination | Paginator | page 0, partial last page, beyond range |
| 12 | Events | EventBus | ordering, unsubscribe, throwing subscriber |
| 13 | Randomness | CodeGenerator | seeded `new Random(42)`, mocked Random, uniqueness |
| 14 | IO/config | ConfigLoader | `Files.createTempFile`, missing file, malformed line |
| 15 | Concurrency | HitCounter | ExecutorService + CountDownLatch |
| + | Contracts | Money, Playlist | equals/hashCode, defensive copies |

Some snippets contain deliberate bugs. After testing each one cold, check `BUGS_ANSWER_KEY.md`.

## TestNG cheat sheet
- `@BeforeMethod` / `@AfterMethod` for per-test setup, `@BeforeClass` for once per class
- `@Test(dataProvider = "name")` with `@DataProvider` returning `Object[][]`
- `@Test(expectedExceptions = X.class, expectedExceptionsMessageRegExp = "...")` or `assertThrows(X.class, () -> ...)`
- `assertEquals(actual, expected)` (TestNG order is actual first); doubles need a delta
- `@Test(timeOut = 1000)` to guard against hangs, `SoftAssert` for several checks in one test
- `@Test(invocationCount = 100, threadPoolSize = 10)` for quick concurrency checks

## Mockito cheat sheet
- `MockitoAnnotations.openMocks(this)` in `@BeforeMethod` (close it in `@AfterMethod`)
- Stub: `when(mock.m()).thenReturn(x)`, `thenThrow(...)`; void methods: `doThrow(...).when(mock).m()`
- Verify: `verify(mock).m(args)`, `times(n)`, `never()`, `verifyNoInteractions(mock)`, `inOrder(...)`
- Matchers: if you use one matcher, all arguments must be matchers (`eq(...)` for literals)
- Capture: `ArgumentCaptor.forClass(X.class)`, then `verify(mock).m(captor.capture())`
- Consecutive results: `when(m.call()).thenThrow(new E()).thenThrow(new E()).thenReturn("ok")`


## Must Do
- ShippingCalculator
- BankAccount
- Paginator
- OrderService
- TokenBucketRateLimiter

## Should do
- TtlCache
- LruCache
- Retry Mechanism
- TextUtils
- CollectionAlgos
- OrderStatus