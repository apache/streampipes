from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

if __name__ == '__main__':
    config = StreamPipesClientConfig(
        credential_provider=StreamPipesApiKeyCredentials(
            username="admin@streampipes.apache.org",
            api_key="Mbias0Uqdytro5fMEMnXXBYM",
        ),
        host_address="localhost",
        https_disabled=True,
        port=8082
    )

    client = StreamPipesClient(client_config=config)
    print(client.dataLakeMeasureApi.get('test').to_pandas())
