@echo off
REM Dungeon Board - Local Development Script for Windows

echo ========================================
echo 🎲 Starting Dungeon Board...
echo ========================================
echo.

REM Start backend in new window
echo 📦 Starting backend...
start "Dungeon Board Backend" cmd /k "cd backend && mvn spring-boot:run"
echo    Backend starting in new window...
echo.

REM Wait for backend to start
echo ⏳ Waiting for backend to start (15 seconds)...
timeout /t 15 /nobreak >nul
echo.

REM Start frontend in new window
echo 🎨 Starting frontend...
start "Dungeon Board Frontend" cmd /k "cd frontend && npm install && npm run dev"
echo    Frontend starting in new window...
echo.

echo ========================================
echo 🎉 Dungeon Board is running!
echo ========================================
echo.
echo    Frontend: http://localhost:5173
echo    Backend:  http://localhost:8080
echo    H2 Console: http://localhost:8080/h2-console
echo.
echo Close this window to keep services running.
echo Close the backend and frontend windows to stop all services.
echo.
pause
