#!/usr/bin/env bash

#bin/kafka-consumer-groups.sh --bootstrap-server 172.31.35.58:9092  --list | awk '{print $1;}' | xargs -I ar bin/kafka-consumer-groups.sh --bootstrap-server 172.31.35.58:9092 --describe --group ar


dir=$(/root/kafka_2.11-1.1.1/bin/kafka-consumer-groups.sh --bootstrap-server 192.168.223.100:9092,192.168.223.101:9092,192.168.223.102:9092  --list | awk '{print $1;}')
for i in $dir
do
#    /root/kafka_2.11-1.1.1/bin/kafka-consumer-groups.sh --bootstrap-server 172.31.35.58:9092 --describe --group $i | grep '92'
    /root/kafka_2.11-1.1.1/bin/kafka-consumer-groups.sh --bootstrap-server 192.168.223.100:9092,192.168.223.101:9092,192.168.223.102:9092 --describe --group $i | grep 'log_huawei'
done