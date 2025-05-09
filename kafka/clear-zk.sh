#!/bin/bash

echo "Waiting for Zookeeper to be ready..."
until (echo ruok | nc zookeeper 2181) &> /dev/null; do
  echo "Waiting for Zookeeper..."
  sleep 1
done
echo "Zookeeper is up!"

echo "Checking if broker exists in Zookeeper..."
if echo "ls /brokers/ids" | zkCli.sh -server zookeeper:2181 2>&1 | grep -q "\[1\]"; then
  echo "Removing stale broker registration..."
  echo "rmr /brokers/ids/1" | zkCli.sh -server zookeeper:2181
  echo "Stale broker removed"
else
  echo "No stale broker found"
fi