package ntsc.cas.cn.pipelines.test;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.forwarder.Forwarder;
import ntsc.cas.cn.pipelines.forwarder.ForwarderFactory;
import ntsc.cas.cn.pipelines.operator.influxdb.OperatorInfluxDB;
import ntsc.cas.cn.pipelines.operator.influxdb.OperatorInfluxDBFactory;
import ntsc.cas.cn.pipelines.operator.mqtt.OperatorMQTT;
import ntsc.cas.cn.pipelines.operator.mqtt.OperatorMQTTFactory;
import ntsc.cas.cn.pipelines.producer.Producer;
import ntsc.cas.cn.pipelines.producer.ProducerFactory;

public class Other {
    public static void main(String[] args) {
        final DataType type = DataType.Other;

        final Producer producer = ProducerFactory.getProducer(type);
        final Thread producerThread = new Thread(producer);
        producerThread.start();

        final Forwarder forwarder = ForwarderFactory.getForwarder(type);
        final Thread forwarderThread = new Thread(forwarder);
        forwarderThread.start();

        final OperatorMQTT operatorMQTT = OperatorMQTTFactory.getOperatorMQTT(type);
        final Thread operatorMQTTThread = new Thread(operatorMQTT);
        operatorMQTTThread.start();

        final OperatorInfluxDB operatorInfluxDB = OperatorInfluxDBFactory.getOperatorInfluxDB(type);
        final Thread operatorInfluxDBThread = new Thread(operatorInfluxDB);
        operatorInfluxDBThread.start();
    }
}
