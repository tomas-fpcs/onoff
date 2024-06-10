#!/bin/bash

docker stop onoff-app
mvn clean install -U &&
  docker build -t onoff-app . &&
  docker run -p 8080:8080 onoff-app