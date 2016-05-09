### How to generate a self-signed cert for Java...
https://www.drissamri.be/blog/java/enable-https-in-spring-boot/

`keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`

spring boot yaml config....
```yaml
server:
  port: 48002
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: changeit
    keyStoreType: PKCS12
    keyAlias: tomcat
```

### How to generate a self-signed cert for nginx and rabbit MQ webstomp...
https://www.digitalocean.com/community/tutorials/how-to-create-an-ssl-certificate-on-nginx-for-ubuntu-14-04

`sudo openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout /etc/nginx/ssl/nginx.key -out /etc/nginx/ssl/nginx.crt`

nginx config...

```
listen 443 ssl;
server_name localhost;

ssl_certificate /etc/nginx/ssl/nginx.crt;
ssl_certificate_key /etc/nginx/ssl/nginx.key;
```
