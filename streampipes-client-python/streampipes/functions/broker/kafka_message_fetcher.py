from confluent_kafka import Consumer


class KafkaMsg:
    """An internal representation of a Kafka message

        Parameters
        ----------
        data: Byte Array
            The received Kafka message as byte array
    """
    def __init__(self, data):
        self.data = data


class KafkaMessageFetcher:
    """Fetches the next message from Kafka

        Parameters
        ----------
        consumer: Consumer
            The Kafka consumer
    """
    def __init__(self, consumer: Consumer):
        self.consumer = consumer

    def __aiter__(self):
        return self

    async def __anext__(self):
        msg = self.consumer.poll(0.1)
        return KafkaMsg(msg.value())

