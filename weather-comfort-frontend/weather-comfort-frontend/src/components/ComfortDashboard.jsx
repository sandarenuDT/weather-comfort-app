
import { useEffect, useMemo, useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { fetchComfortRanking } from '../services/weatherApi'
import CityRow from './CityRow'

const THEME_KEY = 'weather-comfort-theme'

const SORT_OPTIONS = [
  { value: 'rank', label: 'Rank' },
  { value: 'comfortScore', label: 'Comfort score' },
  { value: 'temperature', label: 'Temperature' },
]

export default function ComfortDashboard() {
  const { getAccessTokenSilently } = useAuth0()
  const [cities, setCities] = useState(null)
  const [error, setError] = useState(null)

  const [search, setSearch] = useState('')
  const [sortKey, setSortKey] = useState('rank')
  const [theme, setTheme] = useState(
    () => localStorage.getItem(THEME_KEY) || 'light'
  )

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme)
    localStorage.setItem(THEME_KEY, theme)
  }, [theme])

  useEffect(() => {
    let cancelled = false

    async function load() {
      try {
        const token = await getAccessTokenSilently()
        const data = await fetchComfortRanking(token)
        if (!cancelled) setCities(data)
      } catch (err) {
        if (!cancelled) setError(err.message)
      }
    }

    load()
    return () => {
      cancelled = true
    }
  }, [getAccessTokenSilently])

  const visibleCities = useMemo(() => {
    if (!cities) return null

    const filtered = search.trim()
      ? cities.filter((c) =>
          c.cityName.toLowerCase().includes(search.trim().toLowerCase())
        )
      : cities

    const sorted = [...filtered].sort((a, b) => {
      if (sortKey === 'comfortScore') return b.comfortScore - a.comfortScore
      if (sortKey === 'temperature') return b.temperature - a.temperature
      return a.rank - b.rank
    })

    return sorted
  }, [cities, search, sortKey])

  if (error) {
    return <p className="state-message error">Couldn't load the comfort ranking: {error}</p>
  }

  if (!cities) {
    return <p className="state-message">Loading today's comfort ranking…</p>
  }

  return (
    <>
      <div className="toolbar">
        <div className="toolbar-search">
          <input
            type="text"
            placeholder="Search cities…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            aria-label="Search cities"
          />
        </div>

        <label className="toolbar-sort">
          Sort by
          <select value={sortKey} onChange={(e) => setSortKey(e.target.value)}>
            {SORT_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </label>

        <button
          type="button"
          className="theme-toggle"
          onClick={() => setTheme((t) => (t === 'light' ? 'dark' : 'light'))}
        >
          {theme === 'light' ? 'Dark mode' : 'Light mode'}
        </button>
      </div>

      {visibleCities.length === 0 ? (
        <p className="empty-results">No cities match "{search}".</p>
      ) : (
        <div className="list">
          {visibleCities.map((city) => (
            <CityRow key={city.cityCode} city={city} />
          ))}
        </div>
      )}
    </>
  )
}