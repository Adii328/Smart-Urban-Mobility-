import { useState, useEffect } from 'react'
import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import './App.css'

const API_BASE = 'http://localhost:8080/api'

function App() {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (e) {
        localStorage.removeItem('user')
      }
    }
  }, [])

  const handleLogin = (userData) => {
    setUser(userData)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('user')
  }

  return (
    <div className="app-root">
      <header className="app-header">
        <h1>Smart Urban Mobility Platform</h1>
        <nav className="nav">
          <Link to="/">Dashboard</Link>
          <Link to="/planner">Journey Planner</Link>
          <Link to="/bookings">Bookings</Link>
          {user && user.role === 'ADMIN' && <Link to="/admin">Admin</Link>}
          {user ? (
            <button className="link-btn" onClick={handleLogout}>
              Logout ({user.fullName})
            </button>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<Dashboard user={user} />} />
          <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/planner" element={<JourneyPlanner user={user} />} />
          <Route path="/bookings" element={<BookingsPage user={user} />} />
          <Route path="/admin" element={<AdminPage user={user} />} />
        </Routes>
      </main>
    </div>
  )
}

function Dashboard({ user }) {
  const [journeys, setJourneys] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const res = await fetch(`${API_BASE}/journeys/all`)
        if (!res.ok) throw new Error('Unable to load journeys')
        const data = await res.json()
        setJourneys(data)
      } catch (err) {
        setError(err.message)
      }
    }
    load()
  }, [])

  return (
    <section className="card">
      <h2>Dashboard</h2>
      {user ? (
        <p>
          Welcome, <strong>{user.fullName}</strong> ({user.email})
        </p>
      ) : (
        <p>Please login or register to plan and book journeys.</p>
      )}
      <p className="muted" style={{ marginTop: '0.75rem' }}>
        Available city journeys:
      </p>
      {error && <p className="error">{error}</p>}
      <div className="list">
        {journeys.map((j) => (
          <div key={j.id} className="list-item">
            <div>
              <strong>{j.mode}</strong> {j.source} → {j.destination}
              <div className="muted">
                ₹{j.baseFare} • {j.durationMinutes} min •{' '}
                {j.sustainable ? 'Eco‑friendly' : 'Regular'}
              </div>
            </div>
          </div>
        ))}
        {journeys.length === 0 && !error && (
          <p className="muted">No journeys configured yet.</p>
        )}
      </div>
    </section>
  )
}

function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('test@example.com')
  const [password, setPassword] = useState('pass123')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      })
      if (!res.ok) throw new Error('Invalid credentials')
      const data = await res.json()
      onLogin({
        userId: data.userId,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
      })
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="card narrow">
      <h2>Login</h2>
      <form onSubmit={submit} className="form">
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        {error && <p className="error">{error}</p>}
        <button disabled={loading}>{loading ? 'Logging in…' : 'Login'}</button>
      </form>
    </section>
  )
}

function RegisterPage() {
  const [email, setEmail] = useState('test@example.com')
  const [password, setPassword] = useState('pass123')
  const [fullName, setFullName] = useState('Test User')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, fullName }),
      })
      if (!res.ok) {
        const txt = await res.text()
        throw new Error(txt || 'Registration failed')
      }
      setMessage('Registered successfully. You can now login.')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="card narrow">
      <h2>Register</h2>
      <form onSubmit={submit} className="form">
        <label>
          Full name
          <input value={fullName} onChange={(e) => setFullName(e.target.value)} />
        </label>
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        {message && <p className="success">{message}</p>}
        {error && <p className="error">{error}</p>}
        <button disabled={loading}>
          {loading ? 'Registering…' : 'Register'}
        </button>
      </form>
    </section>
  )
}

function JourneyPlanner({ user }) {
  const [source, setSource] = useState('City Center')
  const [destination, setDestination] = useState('University')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [selected, setSelected] = useState(null)

  const search = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    setResults([])
    setSelected(null)
    try {
      const params = new URLSearchParams({ source, destination })
      const res = await fetch(`${API_BASE}/journeys/search?${params.toString()}`)
      if (!res.ok) throw new Error('Search failed')
      const data = await res.json()
      setResults(data)
      if (data.length === 0) setError('No journeys found for this route.')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="card">
      <h2>Journey Planner</h2>
      {!user && <p className="hint">Login to book journeys after searching.</p>}
      <form onSubmit={search} className="form form-inline">
        <label>
          From
          <input value={source} onChange={(e) => setSource(e.target.value)} />
        </label>
        <label>
          To
          <input
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
          />
        </label>
        <button disabled={loading}>{loading ? 'Searching…' : 'Search'}</button>
      </form>
      {error && <p className="error">{error}</p>}
      <div className="list">
        {results.map((j) => (
          <div
            key={j.id}
            className={
              'list-item' + (selected && selected.id === j.id ? ' selected' : '')
            }
          >
            <div>
              <strong>{j.mode}</strong> {j.source} → {j.destination}
              <div className="muted">
                ₹{j.baseFare} • {j.durationMinutes} min •{' '}
                {j.sustainable ? 'Eco‑friendly' : 'Regular'}
              </div>
            </div>
            <button onClick={() => setSelected(j)}>Select</button>
          </div>
        ))}
      </div>
      {user && selected && (
        <CreateBookingPanel user={user} journey={selected} />
      )}
    </section>
  )
}

function CreateBookingPanel({ user, journey }) {
  const [status, setStatus] = useState('')
  const [error, setError] = useState('')

  const createBooking = async () => {
    setStatus('')
    setError('')
    try {
      const res = await fetch(`${API_BASE}/bookings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: user.userId, journeyId: journey.id }),
      })
      if (!res.ok) throw new Error('Booking failed')
      const data = await res.json()
      setStatus(`Booking created with ID ${data.id}, status ${data.status}`)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="panel">
      <h3>Selected journey</h3>
      <p>
        <strong>{journey.mode}</strong> {journey.source} → {journey.destination}{' '}
        (₹{journey.baseFare})
      </p>
      <button onClick={createBooking}>Book this journey</button>
      {status && <p className="success">{status}</p>}
      {error && <p className="error">{error}</p>}
    </div>
  )
}

function BookingsPage({ user }) {
  const [bookings, setBookings] = useState([])
  const [error, setError] = useState('')

  const load = async () => {
    setError('')
    if (!user) {
      setError('Please login to view your bookings.')
      return
    }
    try {
      const res = await fetch(`${API_BASE}/bookings/user/${user.userId}`)
      if (!res.ok) throw new Error('Unable to load bookings')
      const data = await res.json()
      setBookings(data)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section className="card">
      <h2>Your bookings</h2>
      <button onClick={load}>Refresh</button>
      {error && <p className="error">{error}</p>}
      <div className="list">
        {bookings.map((b) => (
          <div key={b.id} className="list-item">
            <div>
              <strong>{b.journey.mode}</strong> {b.journey.source} →{' '}
              {b.journey.destination}
              <div className="muted">
                Booking #{b.id} • ₹{b.totalFare} • {b.status}
              </div>
            </div>
          </div>
        ))}
        {bookings.length === 0 && !error && (
          <p className="muted">No bookings yet. Use Journey Planner first.</p>
        )}
      </div>
    </section>
  )
}

function AdminPage({ user }) {
  const [activeTab, setActiveTab] = useState('summary')
  const [summary, setSummary] = useState(null)
  const [users, setUsers] = useState([])
  const [bookings, setBookings] = useState([])
  const [journeys, setJourneys] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadSummary = async () => {
    setError('')
    if (!user || user.role !== 'ADMIN') {
      setError('You must log in as an ADMIN (email ending with @admin.com) to view this page.')
      return
    }
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/admin/summary`)
      if (!res.ok) throw new Error('Unable to load admin summary')
      const data = await res.json()
      setSummary(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const loadUsers = async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/admin/users`)
      if (!res.ok) throw new Error('Unable to load users')
      const data = await res.json()
      setUsers(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const loadBookings = async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/admin/bookings`)
      if (!res.ok) throw new Error('Unable to load bookings')
      const data = await res.json()
      setBookings(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const loadJourneys = async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/admin/journeys`)
      if (!res.ok) throw new Error('Unable to load journeys')
      const data = await res.json()
      setJourneys(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const deleteUser = async (id) => {
    if (!confirm('Are you sure you want to delete this user?')) return
    try {
      const res = await fetch(`${API_BASE}/admin/users/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error('Failed to delete user')
      setUsers(users.filter(u => u.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  const deleteBooking = async (id) => {
    if (!confirm('Are you sure you want to delete this booking?')) return
    try {
      const res = await fetch(`${API_BASE}/admin/bookings/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error('Failed to delete booking')
      setBookings(bookings.filter(b => b.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  const deleteJourney = async (id) => {
    if (!confirm('Are you sure you want to delete this journey?')) return
    try {
      const res = await fetch(`${API_BASE}/admin/journeys/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error('Failed to delete journey')
      setJourneys(journeys.filter(j => j.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  useEffect(() => {
    if (activeTab === 'summary' && !summary) loadSummary()
    else if (activeTab === 'users' && users.length === 0) loadUsers()
    else if (activeTab === 'bookings' && bookings.length === 0) loadBookings()
    else if (activeTab === 'journeys' && journeys.length === 0) loadJourneys()
  }, [activeTab])

  return (
    <section className="card">
      <h2>Admin Panel</h2>
      <div className="admin-tabs">
        <button className={activeTab === 'summary' ? 'active' : ''} onClick={() => setActiveTab('summary')}>Summary</button>
        <button className={activeTab === 'users' ? 'active' : ''} onClick={() => setActiveTab('users')}>Users</button>
        <button className={activeTab === 'bookings' ? 'active' : ''} onClick={() => setActiveTab('bookings')}>Bookings</button>
        <button className={activeTab === 'journeys' ? 'active' : ''} onClick={() => setActiveTab('journeys')}>Journeys</button>
      </div>
      {error && <p className="error">{error}</p>}
      {loading && <p>Loading...</p>}

      {activeTab === 'summary' && summary && (
        <div className="admin-grid">
          <div className="admin-tile">
            <h3>Total users</h3>
            <p className="admin-number">{summary.totalUsers}</p>
          </div>
          <div className="admin-tile">
            <h3>Total bookings</h3>
            <p className="admin-number">{summary.totalBookings}</p>
          </div>
          <div className="admin-tile">
            <h3>Confirmed bookings</h3>
            <p className="admin-number">{summary.confirmedBookings}</p>
          </div>
          <div className="admin-tile">
            <h3>Total revenue</h3>
            <p className="admin-number">₹{summary.totalRevenue}</p>
          </div>
        </div>
      )}

      {activeTab === 'users' && (
        <div>
          <h3>Users</h3>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Email</th>
                <th>Full Name</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.email}</td>
                  <td>{user.fullName}</td>
                  <td>{user.role}</td>
                  <td>
                    <button onClick={() => deleteUser(user.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'bookings' && (
        <div>
          <h3>Bookings</h3>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Journey</th>
                <th>Status</th>
                <th>Total Fare</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map(booking => (
                <tr key={booking.id}>
                  <td>{booking.id}</td>
                  <td>{booking.user?.fullName || 'N/A'}</td>
                  <td>{booking.journey?.origin} - {booking.journey?.destination}</td>
                  <td>{booking.status}</td>
                  <td>₹{booking.totalFare}</td>
                  <td>
                    <button onClick={() => deleteBooking(booking.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'journeys' && (
        <div>
          <h3>Journeys</h3>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Source</th>
                <th>Destination</th>
                <th>Mode</th>
                <th>Fare</th>
                <th>Duration</th>
                <th>Sustainable</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {journeys.map(journey => (
                <tr key={journey.id}>
                  <td>{journey.id}</td>
                  <td>{journey.source}</td>
                  <td>{journey.destination}</td>
                  <td>{journey.mode}</td>
                  <td>₹{journey.baseFare}</td>
                  <td>{journey.durationMinutes} min</td>
                  <td>{journey.sustainable ? 'Yes' : 'No'}</td>
                  <td>
                    <button onClick={() => deleteJourney(journey.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

export default App
