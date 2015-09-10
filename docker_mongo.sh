#!/bin/sh
docker run -p 27017:27017 --name play_mongo -d mongo --noprealloc --smallfiles
