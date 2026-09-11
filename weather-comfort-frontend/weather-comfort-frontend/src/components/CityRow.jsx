
import { useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { fetchForecast } from '../services/weatherApi'
import TemperatureTrendChart from './TemperatureTrendChart'

function comfortColor(score) {
  // Interpolate between rust (low) and moss (high) based on score 0-100
  const low = { r: 181, g: 72, b: 52 }   // --comfort-low
  const high = { r: 58, g: 125, b: 103 } // --comfort-high
  const t = Math.max(0, Math.min(100, score)) / 100
  const r = Math.round(low.r + (high.r - low.r) * t)
  const g = Math.round(low.g + (high.g - low.g) * t)
  const b = Math.round(low.b + (high.b - low.b) * t)
  return `rgb(${r}, ${g}, ${b})`
}

export default function CityRow({ city }) {
  const { getAccessTokenSilently } = useAuth0()
  const color = comfortColor(city.comfortScore)

  const [expanded, setExpanded] = useState(false)
  const [forecast, setForecast] = useState(null)
  const [forecastError, setForecastError] = useState(null)
  const [loadingForecast, setLoadingForecast] = useState(false)

  async function handleToggle() {
    const next = !expanded
    setExpanded(next)

    // Fetch on first expand only - forecast doesn't change fast enough
    // to need refetching every time the row is reopened this session.
    if (next && !forecast && !loadingForecast) {
      setLoadingForecast(true)
      setForecastError(null)
      try {
        const token = await getAccessTokenSilently()
        const data = await fetchForecast(token, city.cityCode)
        setForecast(data)
      } catch (err) {
        setForecastError(err.message)
      } finally {
        setLoadingForecast(false)
      }
    }
  }

  return (
    <div className="row" onClick={handleToggle} role="button" tabIndex={0}
      onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); handleToggle() } }}
      aria-expanded={expanded}
    >
      <div className="rank">{city.rank}</div>
      <div className="city-info">
        <p className="city-name">{city.cityName}</p>
        <div className="city-meta">
          <span>{city.description}</span>
          <span>{Math.round(city.temperature)}°C</span>
          <span>{city.humidity}% humidity</span>
          <span>{city.windSpeed} m/s wind</span>
        </div>
      </div>
      <div className="comfort-block">
        <div className="comfort-score" style={{ color }}>
          {city.comfortScore}
        </div>
        <div className="comfort-bar-track">
          <div
            className="comfort-bar-fill"
            style={{ width: `${city.comfortScore}%`, background: color }}
          />
        </div>
      </div>

      {expanded && (
        <div className="row-expand" onClick={(e) => e.stopPropagation()}>
          <p className="forecast-heading">Temperature forecast, next 5 days</p>
          {loadingForecast && <p className="forecast-state">Loading forecast…</p>}
          {forecastError && (
            <p className="forecast-state" style={{ color: 'var(--comfort-low)' }}>
              Couldn't load the forecast: {forecastError}
            </p>
          )}
          {forecast && <TemperatureTrendChart points={forecast} />}
        </div>
      )}
    </div>
  )
}