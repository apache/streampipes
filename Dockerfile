FROM nginx

COPY dist/assets/img/ /usr/share/nginx/html/assets/img/
COPY dist/assets/lib/ /usr/share/nginx/html/assets/lib/
COPY dist/assets/css/ /usr/share/nginx/html/assets/css/
COPY dist/assets/templates/ /usr/share/nginx/html/assets/templates/
COPY dist/index.html /usr/share/nginx/html/
COPY dist/main.bundle.js /usr/share/nginx/html/
COPY dist/main.polyfills.js /usr/share/nginx/html/

COPY site/ /usr/share/nginx/html/docs
COPY javadoc/ /usr/share/nginx/html/javadoc

COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf

RUN chown -R nginx:nginx /usr/share/nginx/html/
