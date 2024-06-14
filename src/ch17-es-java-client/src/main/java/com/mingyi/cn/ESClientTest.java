package com.mingyi.cn;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class ESClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ESClientTest.class);
    ElasticsearchClient m_es_client;
    Product m_product;

    /*
     **@brief:构造函数，目的：初始化
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-10
     */
    ESClientTest() throws URISyntaxException {
        //设置用户名和密码（前提 es8.X部署完毕）
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "Dwgtt1vooBMnHGMSP_z3"));

        //证书位置：elasticsearch-8.2.2/config/certs下，certs是动态生成的，需要手动拷贝的工程路径下。

        String filePath = new File(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("http_ca.crt"))
                .toURI()).getAbsolutePath();
        Path caCertificatePath = Paths.get(filePath);
        try {
            CertificateFactory factory;
            //Encrypted communication 范畴
            //https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/_encrypted_communication.html
            //X.509是密码学里公钥证书的格式标准。X.509证书已应用在包括TLS/SSL在内的众多网络协议里，同时它也用在很多非在线应用场景里，比如电子签名服务。
            //X.509证书里含有公钥、身份信息（比如网络主机名，组织的名称或个体名称等）和签名信息（可以是证书签发机构CA的签名，也可以是自签名）。[1]
            //https://zh.m.wikipedia.org/zh-hans/X.509
            factory = CertificateFactory.getInstance("X.509");
            InputStream is = Files.newInputStream(caCertificatePath);
            Certificate trustedCa;
            trustedCa = factory.generateCertificate(is);

            //在密码学中，PKCS #12 定义了一种归档文件格式，用于实现存储许多加密对象在一个单独的文件中。
            //通常用它来打包一个私钥及有关的 X.509 证书，或者打包信任链的全部项目。[1]
            //https://zh.m.wikipedia.org/zh-hans/PKCS_12
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();

            //构造https 客户端请求访问
            //setSSLHostnameVerifier含义：禁止主机名验证
            RestClientBuilder builder = RestClient.builder(
                    new HttpHost("192.168.56.103", 9200, "https"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE));
            RestClient restClient = builder.build();
            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

            // And create the API client
            m_es_client = new ElasticsearchClient(transport);
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     **@brief:构造函数，目的：初始化
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-10
     */
    ESClientTest(String ipaddress) throws URISyntaxException {

        //设置用户名和密码（前提 es8.X部署完毕）
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "Dwgtt1vooBMnHGMSP_z3"));

        //证书位置：elasticsearch-8.2.2/config/certs下，certs是动态生成的，需要手动拷贝的工程路径下。
        String filePath = new File(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("http_ca.crt"))
                .toURI()).getAbsolutePath();

        Path caCertificatePath = Paths.get(filePath);
        try {
            CertificateFactory factory;
            //Encrypted communication 范畴
            //https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/_encrypted_communication.html
            //X.509是密码学里公钥证书的格式标准。X.509证书已应用在包括TLS/SSL在内的众多网络协议里，同时它也用在很多非在线应用场景里，比如电子签名服务。
            //X.509证书里含有公钥、身份信息（比如网络主机名，组织的名称或个体名称等）和签名信息（可以是证书签发机构CA的签名，也可以是自签名）。[1]
            //https://zh.m.wikipedia.org/zh-hans/X.509
            factory = CertificateFactory.getInstance("X.509");
            InputStream is = Files.newInputStream(caCertificatePath);
            Certificate trustedCa;
            trustedCa = factory.generateCertificate(is);

            //在密码学中，PKCS #12 定义了一种归档文件格式，用于实现存储许多加密对象在一个单独的文件中。
            //通常用它来打包一个私钥及有关的 X.509 证书，或者打包信任链的全部项目。[1]
            //https://zh.m.wikipedia.org/zh-hans/PKCS_12
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();

            //构造https 客户端请求访问
            //setSSLHostnameVerifier含义：禁止主机名验证
            RestClientBuilder builder = RestClient.builder(
                    new HttpHost(ipaddress, 9200, "https"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE));
			
			/*
			 * POST /_security/api_key
				{
				  "name": "java-connect",
				  "role_descriptors": {}
				}
			 * 返回结果如下：
			 * {
                  "id": "HcnMFpABcPKA93mK9U0F",
                  "name": "java-connect",
                  "api_key": "kmYD00NqQNmMI7ZN4l3qEg",
                  "encoded": "SGNuTUZwQUJjUEtBOTNtSzlVMEY6a21ZRDAwTnFRTm1NSTdaTjRsM3FFZw=="
                }
			 * 
			 */
            // 经验证，每次执行结果都不同，会有变化。
            String apiKeyId = "HcnMFpABcPKA93mK9U0F";
            String apiKeySecret = "kmYD00NqQNmMI7ZN4l3qEg";
            String apiKeyAuth =
                    Base64.getEncoder().encodeToString(
                            (apiKeyId + ":" + apiKeySecret)
                                    .getBytes(StandardCharsets.UTF_8));
            Header[] defaultHeaders =
                    new Header[]{new BasicHeader("Authorization",
                            "ApiKey " + apiKeyAuth)};
            builder.setDefaultHeaders(defaultHeaders);

            RestClient restClient = builder.build();
            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

            // And create the API client
            m_es_client = new ElasticsearchClient(transport);
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     **@brief:单个文档的写入
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-10
     */
    void single_write() {
        m_product = new Product("bk-1", "City bike", 123.0);

        IndexResponse response;
        try {
            response = m_es_client.index(i -> i
                    .index("products")
                    .id(m_product.getMsku())
                    .document(m_product)
            );
            logger.info("Indexed with version " + response.version());
        } catch (ElasticsearchException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /*
     **@brief:批量文档的写入
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-10
     */
    void bulk_write() {
        Product m_product = new Product();
        List<Product> products = m_product.fetchProducts();

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Product product : products) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("products")
                            .id(product.getMsku())
                            .document(product)
                    )
            );
        }


        try {
            BulkResponse result = m_es_client.bulk(br.build());
            // Log errors, if any
            if (result.errors()) {
                logger.error("Bulk had errors");
                for (BulkResponseItem item : result.items()) {
                    if (item.error() != null) {
                        logger.error(item.error().reason());
                    }
                }
            } else {
                logger.info("Bulk write success!");
            }
        } catch (ElasticsearchException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     **@brief:指定id检索数据
     **@param：待检索的id值
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-12
     */
    public void searchById(String sid) {

        GetResponse<Product> response;
        try {
            response = m_es_client.get(g -> g
                            .index("products")
                            .id(sid),
                    Product.class
            );
            if (response.found()) {
                Product product = response.source();
                assert product != null;
                logger.info("Product name " + product.getMtype());
            } else {
                logger.info("Product not found");
            }
        } catch (ElasticsearchException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     **@brief:指定关键词的检索
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-12
     */
    public void searchBykeyword() {
        String searchText = "bike";

        SearchResponse<Product> response;
        try {
            response = m_es_client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .match(t -> t
                                            .field("mtype")
                                            .query(searchText)
                                    )
                            ),
                    Product.class
            );

            TotalHits total = response.hits().total();
            assert total != null;
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                logger.info("There are " + total.value() + " results");
            } else {
                logger.info("There are more than " + total.value() + " results");
            }

            List<Hit<Product>> hits = response.hits().hits();
            for (Hit<Product> hit : hits) {
                Product product = hit.source();
                assert product != null;
                logger.info("Found product " + product.getMsku() + ", score " + hit.score());
            }

        } catch (ElasticsearchException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     **@brief:聚合操作
     **@param：无
     **@return：无
     **@author：铭毅天下
     **@date:2022-06-12
     */
    public void aggs_by_histogram() {
        String searchText = "bike";

        try {
            Query query = MatchQuery.of(m -> m
                    .field("mtype")
                    .query(searchText)
            )._toQuery();

            SearchResponse<Void> response = m_es_client.search(b -> b
                            .index("products")
                            .size(0)
                            .query(query)
                            .aggregations("price-histogram", a -> a
                                    .histogram(h -> h
                                            .field("mprice")
                                            .interval(50.0)
                                    )
                            ),
                    Void.class
            );

            List<HistogramBucket> buckets = response.aggregations()
                    .get("price-histogram")
                    .histogram()
                    .buckets().array();

            for (HistogramBucket bucket : buckets) {
                logger.info("There are " + bucket.docCount() +
                        " bikes under " + bucket.key());
            }
        } catch (ElasticsearchException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        ESClientTest esclient;
        //方式一：安全认证方式
        esclient = new ESClientTest();
        //方式二：API key 访问方式
        //esclient = new esclientTest("192.168.2.37");
        esclient.single_write();
        esclient.bulk_write();
        esclient.searchBykeyword();
        esclient.searchById("bk-4");
        esclient.aggs_by_histogram();
        esclient.m_es_client.shutdown();
    }
}
