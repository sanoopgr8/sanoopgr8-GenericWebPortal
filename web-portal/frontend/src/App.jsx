import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom'
import './App.css'
import Login from './Login'
import Signup from './Signup'
import Verify from './Verify'

function Home({ user, setUser }) {
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (user) {
      fetch('/api/hello')
        .then(response => response.text())
        .then(data => setMessage(data))
        .catch(error => console.error('Error fetching data:', error))
    }
  }, [user])

  if (!user) {
    return (
      <div className="container">
        <h1>Web Portal</h1>
        <div className="card">
          <Login onLogin={setUser} />
          <p>Don't have an account?</p>
          <Link to="/signup">
            <button>Sign Up</button>
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="container">
      <h1>Web Portal</h1>
      <div className="card">
        <h2>Welcome, {user}!</h2>
        <p>Backend says:</p>
        <h2 className="message">{message || 'Loading...'}</h2>
        <button onClick={() => setUser(null)}>Logout</button>
      </div>
    </div>
  )
}

function SignupPage() {
  const navigate = useNavigate();

  return (
    <div className="container">
      <h1>Web Portal</h1>
      <div className="card">
        <Signup />
        <p>Already have an account?</p>
        <button onClick={() => navigate('/')}>Back to Login</button>
      </div>
    </div>
  )
}

function App() {
  const [user, setUser] = useState(null)

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home user={user} setUser={setUser} />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/verify" element={<Verify />} />
      </Routes>
    </Router>
  )
}

export default App
