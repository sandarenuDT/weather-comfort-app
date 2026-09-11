# Weather Comfort App

A full-stack weather analytics application that retrieves live weather data for a set of cities, computes a custom **Comfort Index Score** for each one, and presents them ranked from most to least comfortable — behind Auth0 authentication with MFA.

Built for the Fidenz Technologies Trainee Software Engineer assignment.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3, Java 17 |
| Frontend | React + Vite |
| Auth | Auth0 |
| Caching | Caffeine (via Spring Cache) |
| Weather data | OpenWeatherMap API |

---

## Architecture

The backend is an **OAuth2 Resource Server only** — it never renders a login page or talks to Auth0's `/authorize` endpoint. All it does is validate the JWT access token attached to each incoming request (signature, issuer, audience).

The actual login/logout/MFA flow happens entirely in the frontend, using the Auth0 React SDK, which redirects the user to Auth0's Universal Login page and returns with an access token. That token is what gets sent as `Authorization: Bearer <token>` on every API call.

```
User → React frontend → Auth0 Universal Login → (MFA via email) → back to frontend with token
Frontend → Spring Boot API (Bearer token) → JWT validated → OpenWeatherMap → Comfort Index computed → ranked JSON response
```

---

## Setup Instructions

### Prerequisites
- Java 17+, Maven
- Node.js 18+
- An OpenWeatherMap API key
- An Auth0 tenant with:
  - An **API** created (used as the `audience`)
  - A **Single Page Application** created for frontend login
  - Sign-ups disabled, with a manually created whitelisted user
  - Email MFA enabled

### Backend

```bash
cd backend
```

Set the following as environment variables (a `.env` file works with a dotenv loader, or export them in your shell):

```
AUTH0_ISSUER_URI=https://YOUR_TENANT.us.auth0.com/
AUTH0_AUDIENCE=https://weather-comfort-api
OPENWEATHER_API_KEY=your_openweathermap_key
```

Run:

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
```

Create a `.env` file in the frontend root:

```
VITE_API_BASE_URL=http://localhost:8080
VITE_AUTH0_DOMAIN=YOUR_TENANT.us.auth0.com
VITE_AUTH0_CLIENT_ID=your_spa_client_id
VITE_AUTH0_AUDIENCE=https://weather-comfort-api
```

Run:

```bash
npm run dev
```

The app starts on `http://localhost:5173`.

### Test user

```
Email: careers@fidenz.com
Password: Pass#fidenz
```

Signing in with any other, non-whitelisted email is rejected before a login form even appears, since public sign-ups are disabled at the Auth0 connection level.

---

## Comfort Index Formula

Each city's Comfort Index is a **0–100 score** computed server-side, combining four weather parameters. Rather than scoring against a single "ideal" number, each parameter is scored against an **ideal range** — comfort isn't a single point, it's a band people generally find pleasant, with a penalty that scales the further a reading falls outside it.

```
ComfortIndex = 0.40 × TemperatureScore
             + 0.30 × HumidityScore
             + 0.15 × WindScore
             + 0.15 × PressureScore
```

| Parameter | Ideal range | Penalty outside the range | Weight |
|---|---|---|---|
| Temperature | 18–24 °C | −4 points per °C | 40% |
| Humidity | 30–60% | −2 points per % | 30% |
| Wind speed | ≤ 5.0 m/s | −8 points per m/s above | 15% |
| Pressure | 1000–1020 hPa | −3 points per hPa | 15% |

All four sub-scores are clamped to `[0, 100]` before weighting, and the final weighted total is clamped again for safety.

### Reasoning behind the weights

- **Temperature (40%)** is the single biggest driver of how comfortable a place feels day to day — it gets the largest weight because it's the parameter people notice and react to first (dressing for it, deciding whether to go outside).
- **Humidity (30%)** comes second because it directly changes how a given temperature *feels* — high humidity makes heat oppressive and makes cold feel damp, so it's weighted heavily but still behind raw temperature.
- **Wind (15%)** and **pressure (15%)** are weighted equally and lower, since both matter but in a more situational way: wind mainly affects comfort at higher speeds (hence a one-sided penalty — only wind *above* the calm threshold is penalized, since light air movement isn't uncomfortable), and pressure has a real but subtler effect, more associated with headaches/fatigue in sensitive people than universal discomfort.

### Trade-offs considered

- **Band vs. single ideal point**: an earlier design considered scoring against one "perfect" number (e.g. exactly 21°C). A range better reflects how people actually experience comfort — nobody meaningfully prefers exactly 21°C over 20°C, but everybody notices the difference between 21°C and 35°C.
- **Linear penalty vs. curve**: a linear point-per-unit penalty was chosen over an exponential/logarithmic curve for interpretability — the formula's behavior is easy to explain and reason about (an interview requirement for this assignment), at the cost of not modeling the possibility that extreme discomfort might compound non-linearly (e.g. 40°C plausibly feels disproportionately worse than 35°C, not just proportionately worse).
- **Not including cloudiness/visibility/dew point**: the assignment allows any 3+ parameters. Four were chosen (temperature, humidity, wind, pressure) as the ones with the clearest, most independent effect on physical comfort; cloudiness and visibility were left out to avoid double-counting effects already captured by temperature and humidity, and dew point wasn't used since it isn't directly returned by the API and would need to be derived, adding complexity without a clear comfort-modeling benefit over humidity alone.

---

## Caching Design

Raw weather API responses are cached using **Caffeine** via Spring's cache abstraction:

- Single named cache (`weatherCache`)
- TTL: 5 minutes by default, configurable via `openweather.cache-ttl-minutes`
- Bounded to 200 entries (`maximumSize`), evicting least-recently-used entries beyond that
- Stats recording enabled (`.recordStats()`), which backs the `/api/cache/status` debug endpoint

This keeps the app well within OpenWeatherMap's rate limits during normal use — with 8–10 cities polled repeatedly by the dashboard, an uncached setup would burn through free-tier request limits quickly. A 5-minute TTL was chosen as a balance: weather conditions don't meaningfully change minute-to-minute, but the data still feels reasonably "live" to someone refreshing the dashboard.

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/weather` | Ranked comfort index dashboard for all cities |
| GET | `/api/cache/status` | Debug endpoint: cache hit/miss and size info |
| GET | `/api/weather/forecast/{cityCode}` | 5-day/3-hour temperature forecast for one city (bonus) |

All endpoints require a valid Auth0-issued Bearer token with the correct audience.

---

## Bonus Features Implemented

- **Dark mode** — toggle in the toolbar, preference persisted in `localStorage`
- **Sort & filter** — search by city name, sort by rank / comfort score / temperature, all client-side
- **Temperature trend graphs** — click a city row to expand an inline chart of its 5-day forecast

---

## Known Limitations

- `cities.json` currently contains **8 cities**; the assignment specifies a minimum of 10. Two more city codes need to be added before submission.
- The forecast endpoint currently has no separate cache TTL of its own distinct from the current-weather cache — under sustained use this could be revisited to cache forecast data for longer, since it changes less often than current conditions.
- No automated integration tests yet for the Auth0-secured endpoints; current test coverage focuses on the Comfort Index calculation logic in isolation.
- Comfort Index parameters (temperature, humidity, wind, pressure) don't account for precipitation type/intensity — a city with light rain and otherwise ideal conditions can currently score identically to a dry city with the same readings.

---

## Setup instructions recap for reviewers

1. Clone the repo
2. Follow **Setup Instructions** above for both `backend/` and `frontend/`
3. Log in with the test user (`careers@fidenz.com` / `Pass#fidenz`), completing email MFA when prompted
4. Dashboard loads automatically after login
