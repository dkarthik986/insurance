@echo off
cd /d "%~dp0frontend"
echo Starting InsureDesk shell at http://localhost:4000 and MFEs on 4001-4007
npm run dev:all
