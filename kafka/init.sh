#!/bin/bash
set -e

kafka-topics --bootstrap-server kafka:9092 --list

kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic meta-requests --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic meta-responses --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic scheduler1-pinger --partitions 2 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic incidents --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic tgbog-sender --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic email-sender --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic sms-sender --partitions 1 --replication-factor 1

kafka-topics --bootstrap-server kafka:9092 --list