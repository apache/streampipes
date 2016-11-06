FROM ubuntu:14.04

RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install apache2 -y
RUN apt-get install supervisor -y

RUN echo `apache2 -v`

COPY ./apache2.conf /etc/apache2/apache2.conf

RUN cd /etc/apache2 && a2enmod proxy_http
RUN cd /etc/apache2 && a2enmod proxy_wstunnel

COPY ./supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN chmod +x /usr/local/bin/httpd-foreground
EXPOSE 80

CMD ["/usr/bin/supervisord"]
