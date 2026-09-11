// // const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

// // export async function fetchComfortRanking(accessToken) {
// //   const response = await fetch(`${API_BASE_URL}/api/weather`, {
// //     headers: {
// //       Authorization: `Bearer ${accessToken}`,
// //     },
// //   })

// //   if (!response.ok) {
// //     throw new Error(`Request failed with status ${response.status}`)
// //   }

// //   return response.json()
// // }

// // export async function fetchCacheStatus(accessToken) {
// //   const response = await fetch(`${API_BASE_URL}/api/cache/status`, {
// //     headers: {
// //       Authorization: `Bearer ${accessToken}`,
// //     },
// //   })

// //   if (!response.ok) {
// //     throw new Error(`Request failed with status ${response.status}`)
// //   }

// //   return response.json()
// // }
// const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

// export async function fetchComfortRanking(accessToken) {
//   const response = await fetch(`${API_BASE_URL}/api/weather`, {
//     headers: {
//       Authorization: `Bearer ${accessToken}`,
//     },
//   })

//   if (!response.ok) {
//     throw new Error(`Request failed with status ${response.status}`)
//   }

//   return response.json()
// }

// export async function fetchCacheStatus(accessToken) {
//   const response = await fetch(`${API_BASE_URL}/api/cache/status`, {
//     headers: {
//       Authorization: `Bearer ${accessToken}`,
//     },
//   })

//   if (!response.ok) {
//     throw new Error(`Request failed with status ${response.status}`)
//   }

//   return response.json()
// }

// // New: powers the temperature trend chart bonus feature.
// // Expects the backend to return an array of { time, temp } points
// // (see the ForecastController addition provided alongside this file).
// export async function fetchForecast(accessToken, cityCode) {
//   const response = await fetch(`${API_BASE_URL}/api/weather/forecast/${cityCode}`, {
//     headers: {
//       Authorization: `Bearer ${accessToken}`,
//     },
//   })

//   if (!response.ok) {
//     throw new Error(`Request failed with status ${response.status}`)
//   }

//   return response.json()
// }

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export async function fetchComfortRanking(accessToken) {
  const response = await fetch(`${API_BASE_URL}/api/weather`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`)
  }

  return response.json()
}

export async function fetchCacheStatus(accessToken) {
  const response = await fetch(`${API_BASE_URL}/api/cache/status`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`)
  }

  return response.json()
}

// New: powers the temperature trend chart bonus feature.
// Expects the backend to return an array of { time, temp } points
// (see the ForecastController addition provided alongside this file).
export async function fetchForecast(accessToken, cityCode) {
  const response = await fetch(`${API_BASE_URL}/api/weather/forecast/${cityCode}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`)
  }

  return response.json()
}