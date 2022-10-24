#!/bin/bash

mvn clean install
cd server/target
tar -xvf tpe2-g9-server-1.0-SNAPSHOT-bin.tar.gz
cd tpe2-g9-server-1.0-SNAPSHOT
chmod u+x run-server.sh


cd ..
cd ..
cd ..
cd client/target
tar -xvf tpe2-g9-client-1.0-SNAPSHOT-bin.tar.gz
cd tpe2-g9-client-1.0-SNAPSHOT
chmod u+x query*


