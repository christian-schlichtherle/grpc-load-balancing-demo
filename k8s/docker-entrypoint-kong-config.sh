#!/bin/sh

http get kong-admin:8001 && \
http --ignore-stdin --form put  kong-admin:8001/services/$API-server host=$API-server port=50505 protocol=tcp && \
http --ignore-stdin --form post kong-admin:8001/services/$API-server/routes name=$API-server sources[1].ip=0.0.0.0/0 protocols=tcp
