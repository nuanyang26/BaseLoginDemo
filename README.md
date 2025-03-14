重置密码方案

### 方案一

用户先带着验证码请求对应接口，然后后端存储对应用户已经通过的标记，用户填写新的密码之后然后请求重置密码的接口，接口验证是否已经通过，然后才重置密码

### 方案二

用户带着验证码请求对应接口，然后后端仅对验证码是否正确进行验证，用户填写新的密码之后，请求重置密码接口，不仅需要带上密码还要之前的验证码一起，然后再次验证验证码如果正确，就重置密码

**本项目采用方案二，session框架采用方案一**

### docker依赖

```shell
docker run -id \
--restart=always \
--name=BaseLoginDemo-rabbitmq \
 --mount type=volume,source=v_BaseLoginDemo-rabbitmq,target=/var/lib/rabbitmq \
-p 15672:15672 -p 5672:5672 \
-e RABBITMQ_DEFAULT_USER=admin \
-e RABBITMQ_DEFAULT_PASS=admin \
rabbitmq:3.12.0-management
```

```shell
docker run -d \
--restart=always \
-p 16379:6379 \
--name BaseLoginDemo-redis \
redis:7.0.12  \
--requirepass admin
```