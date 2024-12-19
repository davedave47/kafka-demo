@echo off
docker compose down

rmdir /s /q datanode1
rmdir /s /q datanode2
rmdir /s /q datanode3
rmdir /s /q namenode