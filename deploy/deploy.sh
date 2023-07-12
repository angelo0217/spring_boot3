#!/bin/sh

echo "------------------------------------------------"
echo "Remove Old Image : world-cup"
echo "------------------------------------------------"
docker rmi demo-spring:0.0.1

echo "------------------------------------------------"
echo "Build New Image : world-cup"
echo "------------------------------------------------"
docker build --network=host --tag demo-spring:0.0.1 --file ./Dockerfile ../


echo "------------------------------------------------"
echo "start server"
echo "------------------------------------------------"
docker-compose -f demo_spring3.yml up
