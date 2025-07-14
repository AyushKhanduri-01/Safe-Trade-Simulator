# 💼 Safe Trade Simulator - Paper Trading Platform

This project is a backend service for a **paper trading platform** that simulates real-world stock trading using **virtual funds and live market data**. It allows users to experience trading without financial risk, helping them build skills, test strategies, and develop financial discipline.

---

## 🚀 Features

### 🔐 Authentication & Security
- Secure user **registration and login**
- Role-based access using **Spring Security** and **JWT authentication**

### 📊 Real-Time Market Integration
- Connects with **NSE F&O live data** via external broker APIs
- Caches market instruments and prices with **Redis** for performance

### 📈 Virtual Trading System
- Simulated **buy/sell** order execution using virtual funds
- Tracks open and closed positions
- Maintains **full trade history** and portfolio data

### 📋 Dashboard Functionality
- API endpoints to support:
  - Account overview
  - Trade tracking
  - Watchlist management
  - Transaction history

### 🧰 Tools for Analysis & Risk Management
- Provides support for stop-loss and take-profit logic
- Backend-ready for integration with performance analytics (charts, summaries, etc.)

---

## 🛠️ Technology Stack

| Component          | Tech Used            |
|--------------------|----------------------|
| Language           | Java                 |
| Framework          | Spring Boot          |
| Security           | Spring Security, JWT |
| Cache              | Redis                |
| Database           | MongoDB (NoSQL)      |
| External API       | Broker API    |
| Build Tool         | Maven                |
| Testing & Tools    | Postman, Git         |



