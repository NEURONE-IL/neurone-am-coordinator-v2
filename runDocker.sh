#!/bin/bash

sudo docker stop ctr_coordinator
sudo docker rm ctr_coordinator
sudo docker rmi img_coordinator


sudo docker build --rm -t img_coordinator:latest .

sudo docker run  --network="host" -p 8080:8080  --name  ctr_coordinator  -d img_coordinator
