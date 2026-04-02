# RestAssured API Test Framework

A production-ready Java API test framework built on **RestAssured 5**, **TestNG**, **Allure**, and **Jackson**.  
Targets the public [JSONPlaceholder](https://jsonplaceholder.typicode.com) API for demonstration.



---

## ⚙️ Prerequisites

| Tool        | Version  |
|-------------|----------|
| Java        | 17+      |
| Maven       | 3.8+     |
| Allure CLI  | 2.x (optional, for report serving) |

---

## ▶️ Running Tests

```bash
# Run full regression suite
mvn clean test

# Run on QA environment
mvn clean test -Denv=qa

# Run only smoke tests
mvn clean test -Dgroups=smoke

# Run only contract / schema tests
mvn clean test -Dgroups=contract

# Override base URL at runtime (e.g. for staging)
mvn clean test -Dbase.url=https://staging.myapi.com

# Run a single test class
mvn clean test -Dtest=PostsApiTest
```

---

## 📊 Reporting

### Allure (recommended)
```bash
# Generate report after test run
# Reports files are generated automatically after test run,
# if they are not generate then use this command
mvn allure:report

# Serve report on localhost
mvn allure:serve
```

### Console Summary
The `ExtentReportManager` listener prints a ✔/✘/⊘ summary to stdout at the end of every suite run.

### Logs
Structured logs are written to:
- **Console** — all levels
- **`target/logs/test-run.log`** — rolling daily file, 7-day retention

---

## 🌍 Environment Configuration

Create a `config-{env}.properties` file in `src/test/resources/` and activate it with `-Denv={env}`.

| Property               | Default                                    | Description                      |
|------------------------|--------------------------------------------|----------------------------------|
| `base.url`             | `https://jsonplaceholder.typicode.com`     | Base URI for all requests        |
| `connection.timeout.ms`| `10000`                                    | Connection timeout in ms         |
| `read.timeout.ms`      | `30000`                                    | Read timeout in ms               |
| `logging.enabled`      | `true`                                     | Toggle RestAssured request logs  |
| `auth.token`           | _(empty)_                                  | Bearer token for auth headers    |

Any property can also be overridden via `-D` JVM system properties (CI/CD friendly).

---

## 🔑 Key Design Decisions

| Concern            | Approach                                                                    |
|--------------------|-----------------------------------------------------------------------------|
| **Config**         | `ConfigManager` singleton — env-specific `.properties` + system property overrides |
| **Spec reuse**     | `RestAssuredConfig.getBaseSpec()` centralises base URL, headers, SSL, Allure filter |
| **Test data**      | `TestDataBuilder` + JavaFaker — no hardcoded strings in tests               |
| **Assertions**     | `ResponseValidator` — descriptive failure messages, no scattered `assertThat` chains |
| **Serialization**  | `JsonUtils` wraps Jackson — clean `fromResponse(response, Post.class)` calls |
| **Schema tests**   | `SchemaValidator` + classpath JSON Schema files — contract-testing made easy |
| **Retry**          | `RetryListener` auto-applies `RetryAnalyzer` (2 retries) to all tests      |
| **Parallelism**    | `parallel="classes"` with `thread-count="3"` in testng.xml                 |
| **Reporting**      | Allure annotations + `AllureRestAssured` filter attach full req/resp to reports |

---

## ➕ Extending the Framework

### Add a new endpoint
1. Add path constants to `ApiConstants`
2. Create a POJO in `models/` if a new resource type is needed
3. Add `buildXxx()` methods to `TestDataBuilder`
4. Create `XxxApiTest extends BaseTest`

### Add a new environment
1. Create `src/test/resources/config-prod.properties`
2. Run with `mvn test -Denv=prod`

### Add JSON Schema validation to a test
1. Drop a `*.json` schema into `src/test/resources/schemas/`
2. Call `SchemaValidator.validate(response, "my-schema.json")` in your test
