#!/bin/sh

echo "------------------------------------------------"
echo "Remove Old Image : demo-spring"
echo "------------------------------------------------"
docker rmi demo-spring:0.0.1

echo "------------------------------------------------"
echo "Build New Image : demo-spring"
echo "------------------------------------------------"
docker build --network=host --tag demo-spring:0.0.1 --file ./Dockerfile ../


echo "------------------------------------------------"
echo "shutdown server"
echo "------------------------------------------------"
docker-compose -f demo_spring3.yml down

echo "------------------------------------------------"
echo "start server"
echo "------------------------------------------------"
docker-compose -f demo_spring3.yml up -d
