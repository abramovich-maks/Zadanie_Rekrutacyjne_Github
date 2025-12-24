# GitHub Discovery Proxy

A simple REST proxy service that retrieves **non-fork GitHub repositories** for a given GitHub user.

---

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.1**
- **Gradle**
- **RestTemplate**
- **WireMock & MockMvc** (integration testing)

---

## API

### Get User Repositories

Returns a list of all **non-fork** repositories for a specific GitHub user.

**Request**
```http
GET /api/github/{username}/repos
```

**Example**
```http
GET /api/github/abramovich-maks/repos
```

## Configuration

The application supports an optional GitHub token to increase API rate limits.

Set the environment variable:

```bash
export GITHUB_TOKEN=your_github_token
```

If the token is not provided, the application will still work with GitHub anonymous rate limits.

---

## Run the Application

```bash
./gradlew bootRun
```

The application will start at:

```text
http://localhost:8080
```

---

## Run Tests

```bash
./gradlew clean test
```

---

## Notes

- Forked repositories are excluded from the response
- GitHub API base URL: `https://api.github.com`
- No credentials or secrets are stored in the source code
- The application can be safely started both from IDE and via Gradle CLI

