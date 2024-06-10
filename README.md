# 《一本书讲透Elasticsearch》学习笔记

原书源代码地址：https://github.com/mingyitianxia/elasticsearch-made-easy

## 环境安装

1. 下载`Elasticsearch8.12.2`  
下载地址：https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.12.2-linux-x86_64.tar.gz
   
2. 下载`Kibana8.12.2`  
下载地址：https://artifacts.elastic.co/downloads/kibana/kibana-8.12.2-linux-x86_64.tar.gz
   
3. 创建`elasticsearch`用户
```shell
groupadd elasticsearch
useradd -m -g elasticsearch elasticsearch
```

4. 在`/home/elasticsearch`目录下解压，启动`Elasticsearch`  
- 在`root`用户下
```shell
cd /home/elasticsearch
tar -xvf elasticsearch-8.12.2-linux-x86_64.tar.gz
chown -R elasticsearch:elasticsearch elasticsearch-8.12.2
su elasticsearch
```

- 在`elasticsearch`用户下
```shell
cd elasticsearch-8.12.2/
./bin/elasticsearch &
```

5. 在`/root`目录下解压，启动`Kibana`
```shell
tar -xvf kibana-8.12.2-linux-x86_64.tar.gz
cd kibana-8.12.2
./bin/kibana --allow-root &
```

6. 在`elasticsearch`用户下，下载IK分词器
```shell
cd /home/elasticsearch
cd elasticsearch-8.12.2/
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.12.2/elasticsearch-analysis-ik-8.12.2.zip
```

7. 重启`Elasticsearch`