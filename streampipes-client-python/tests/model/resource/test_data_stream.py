from unittest import TestCase

from streampipes.model.resource import DataStream


class TestDataStreamWorkaround(TestCase):
    """
    Testcase that assures behavior of workaround introduced
    as a temporary fix for https://github.com/apache/streampipes/issues/1245
    Needs to be removed as soon as possible
    """

    def test_nats_case(self):
        data_stream_def = {
            "elementId": "some-random-id",
            "eventGrounding": {
                "transportProtocols": [
                    {
                        "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
                        "brokerHostname": "broker-host-name",
                        "topicDefinition": {
                            "@class": "some-class-name",
                            "actualTopicName": "actual-topic-name"
                        },
                        "port": 50
                    }
                ]
            }
        }

        data_stream = DataStream.parse_obj(data_stream_def)

        self.assertEqual(50, data_stream.to_dict()["eventGrounding"]["transportProtocols"][0]["port"])

    def test_kafka_case(self):

        data_stream_def = {
            "elementId": "some-random-id",
            "eventGrounding": {
                "transportProtocols": [
                    {
                        "@class": "org.apache.streampipes.model.grounding.KafkaTransportProtocol",
                        "brokerHostname": "broker-host-name",
                        "topicDefinition": {
                            "@class": "some-class-name",
                            "actualTopicName": "actual-topic-name"
                        },
                        "kafkaPort": 50
                    }
                ]
            }
        }

        data_stream = DataStream.parse_obj(data_stream_def)

        self.assertEqual(50, data_stream.to_dict()["eventGrounding"]["transportProtocols"][0]["kafkaPort"])
