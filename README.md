# 《一本书讲透Elasticsearch》学习笔记

本人历时2周时间才看完这本书，对这本书的整体感觉如下：
1. 书中整体介绍了Elasticsearch的各个知识点，包括索引、映射、分词、预处理、文档、脚本、检索、聚合等操作，很适合体系化地学习。
2. 本书介绍的3个项目，其中第3个项目（Elastic Stack日志系统实战）可以本地化部署，其他两个项目不太详细，无法支持本地化部署学习。
3. 通过这本书的学习，也总结了在环境安装时遇到的坑，提供8.12.2版本的部署安装步骤，方便大家学习。

原书源代码地址：https://github.com/mingyitianxia/elasticsearch-made-easy

## 笔记地址

在线阅读地址：https://relph1119.github.io/elasticsearch-in-a-nutshell/#/

## 环境安装（基于CentOS7.6）

### 安装Elasticsearch8.12.2

1. 下载`Elasticsearch8.12.2`  
下载地址：https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.12.2-linux-x86_64.tar.gz

2. 创建`elasticsearch`用户
```shell
groupadd elasticsearch
useradd -m -g elasticsearch elasticsearch
```

3. 在`root`用户下，解压到`/home/elasticsearch`目录下

```shell
cd /home/elasticsearch
tar -xvf elasticsearch-8.12.2-linux-x86_64.tar.gz
chown -R elasticsearch:elasticsearch elasticsearch-8.12.2
su elasticsearch
```

4. 在`elasticsearch`用户下，下载IK分词器
```shell
cd /home/elasticsearch
cd elasticsearch-8.12.2/
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.12.2/elasticsearch-analysis-ik-8.12.2.zip
```

5. 在`elasticsearch`用户下启动`Elasticsearch`

```shell
cd elasticsearch-8.12.2/
./bin/elasticsearch &
```

### 安装Kibana8.12.2

1. 下载`Kibana8.12.2`  
下载地址：https://artifacts.elastic.co/downloads/kibana/kibana-8.12.2-linux-x86_64.tar.gz
   
2. 在`/root`目录下解压，启动`Kibana`
```shell
tar -xvf kibana-8.12.2-linux-x86_64.tar.gz
cd kibana-8.12.2
./bin/kibana --allow-root &
```

### 安装Metricbeat8.12.2

1. 下载`Metricbeat8.12.2`
下载地址：https://artifacts.elastic.co/downloads/beats/metricbeat/metricbeat-8.12.2-linux-x86_64.tar.gz
   
2. 在`/root`目录下解压
```shell
tar -xvf metricbeat-8.12.2-linux-x86_64.tar.gz
```

3. 启动`X-Pack`插件
```shell
./metricbeat modules enable elasticsearch-xpack
```

4. 配置`elasticsearch-xpack.yml`
```yaml
- module: elasticsearch
  xpack.enabled: true
  period: 10s
  hosts: ["http://192.168.56.103:9200"]
  username: "elastic"
  password: ""
```

5. 配置`metricbeat.yml`
```yaml
setup.kibana:

  # Kibana Host
  # Scheme and port can be left out and will be set to the default (http and 5601)
  # In case you specify and additional path, the scheme is required: http://localhost:5601/path
  # IPv6 addresses should always be defined as: https://[2001:db8::1]:5601
  host: "192.168.56.103:5601"
  username: "elastic"
  password: "Dwgtt1vooBMnHGMSP_z3"

output.elasticsearch:
  # Array of hosts to connect to.
  hosts: ["192.168.56.103:9200"]

  # Performance preset - one of "balanced", "throughput", "scale",
  # "latency", or "custom".
  preset: balanced

  # Protocol - either `http` (default) or `https`.
  protocol: "https"

  # Authentication credentials - either API key or username/password.
  #api_key: "id:api_key"
  username: "elastic"
  password: ""

  ssl.verification_mode: none
```

6. 启动`Metricbeat`
```shell
nohup ./metricbeat & > /dev/null 2>&1
```