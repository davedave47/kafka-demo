@echo off

:: Bring down the Docker Compose services
docker compose down

:: Wait for a few seconds
timeout /t 3 /nobreak

:: Delete contents of ./air, ./water, ./earth, and ./cloud directories
rd /s /q air
rd /s /q water
rd /s /q earth
rd /s /q cloud