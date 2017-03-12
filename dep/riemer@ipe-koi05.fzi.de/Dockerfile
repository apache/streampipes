FROM kibana:4.1.2

RUN apt-get update && apt-get install -y netcat

COPY entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

#RUN kibana plugin --install elastic/sense

CMD ["/tmp/entrypoint.sh"]
