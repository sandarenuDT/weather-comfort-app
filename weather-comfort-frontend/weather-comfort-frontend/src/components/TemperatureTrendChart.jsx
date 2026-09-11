
const WIDTH = 480
const HEIGHT = 120
const PADDING = { top: 12, right: 12, bottom: 20, left: 12 }

export default function TemperatureTrendChart({ points }) {
  if (!points || points.length === 0) return null

  const temps = points.map((p) => p.temp)
  const minTemp = Math.min(...temps)
  const maxTemp = Math.max(...temps)
  // Avoid a flat/degenerate scale when forecast temps barely change
  const range = Math.max(maxTemp - minTemp, 1)

  const plotWidth = WIDTH - PADDING.left - PADDING.right
  const plotHeight = HEIGHT - PADDING.top - PADDING.bottom

  const xFor = (i) =>
    PADDING.left + (i / (points.length - 1 || 1)) * plotWidth
  const yFor = (temp) =>
    PADDING.top + plotHeight - ((temp - minTemp) / range) * plotHeight

  const linePath = points
    .map((p, i) => `${i === 0 ? 'M' : 'L'} ${xFor(i).toFixed(1)} ${yFor(p.temp).toFixed(1)}`)
    .join(' ')

  // Label every 4th point (roughly every 12h at 3h-forecast intervals) to avoid crowding
  const labelEvery = Math.max(1, Math.ceil(points.length / 6))

  return (
    <svg
      className="forecast-chart"
      viewBox={`0 0 ${WIDTH} ${HEIGHT}`}
      role="img"
      aria-label={`Temperature forecast ranging from ${Math.round(minTemp)} to ${Math.round(maxTemp)} degrees`}
    >
      <line
        className="chart-axis"
        x1={PADDING.left}
        y1={HEIGHT - PADDING.bottom}
        x2={WIDTH - PADDING.right}
        y2={HEIGHT - PADDING.bottom}
      />

      <path className="chart-line" d={linePath} />

      {points.map((p, i) => (
        <circle key={p.time} className="chart-point" cx={xFor(i)} cy={yFor(p.temp)} r="2.5" />
      ))}

      {points.map((p, i) =>
        i % labelEvery === 0 ? (
          <text
            key={`label-${p.time}`}
            className="chart-label"
            x={xFor(i)}
            y={HEIGHT - 4}
            textAnchor="middle"
          >
            {p.label}
          </text>
        ) : null
      )}
    </svg>
  )
}