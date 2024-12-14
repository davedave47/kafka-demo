@echo off
docker-compose down

timeout /t 10

rmdir /s /q air
rmdir /s /q water
rmdir /s /q earth
rmdir /s /q spark\logs
rmdir /s /q datanode
rmdir /s /q namenode