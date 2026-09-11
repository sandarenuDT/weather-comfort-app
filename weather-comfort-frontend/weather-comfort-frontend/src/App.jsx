import { useAuth0 } from '@auth0/auth0-react'
import LoginButton from './components/LoginButton'
import LogoutButton from './components/LogoutButton'
import ComfortDashboard from './components/ComfortDashboard'

const today = new Date().toLocaleDateString(undefined, {
  weekday: 'long',
  month: 'long',
  day: 'numeric',
})

export default function App() {
  const { isAuthenticated, isLoading, user } = useAuth0()

  return (
    <div className="page">
      <header className="masthead">
        <h1>Comfort Index</h1>
        <span className="date">{today}</span>
      </header>

      {isLoading && <p className="state-message">Checking your session…</p>}

      {!isLoading && !isAuthenticated && (
        <div className="auth-bar">
          <LoginButton />
        </div>
      )}

      {!isLoading && isAuthenticated && (
        <>
          <div className="auth-bar">
            <span className="user-tag">Signed in as {user?.email}</span>
            <LogoutButton />
          </div>
          <ComfortDashboard />
        </>
      )}
    </div>
  )
}
