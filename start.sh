#!/bin/bash

# Pull new changes
git pull

# Prepare Jar
gradlew clean build -x test jar

# create new docker image
docker build -t acvik/databuilder .
docker push acvik/databuilder

# Ensure, that docker-compose stopped
docker-compose stop

# clear all images
docker-compose down --rmi all

# Add environment variables
#export BOT_NAME=$1
#export BOT_TOKEN=$2

# Start new deployment
docker-compose up --build -d