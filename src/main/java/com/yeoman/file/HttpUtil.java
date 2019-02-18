package com.yeoman.file;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * code is far away from bug with the animal protecting
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　  ┃
 * ┃　　　━　　  ┃
 * ┃　┳┛　┗┳  ┃
 * ┃　　　　　　  ┃
 * ┃　　　┻　　  ┃
 * ┃　　　　　    ┃
 * ┗━┓　　  ┏━┛
 * 　  ┃　　　┃神兽保佑
 * 　┃　　　┃代码无BUG！
 * 　  ┃　　　┗━━━┓
 * 　  ┃　　　　     ┣┓
 * 　  ┃　　　　　   ┏┛
 * 　  ┗┓┓┏━┓┓┏┛
 * 　    ┃┫┫　┃┫┫
 * 　    ┗┻┛　┗┻┛
 *
 * @Description :
 * ---------------------------------
 * @Author : Yeoman
 * @Date : Create in 2018/11/26
 */
public class HttpUtil {

        /**
         * 根据请求类型生成对应的请求，除了这四种请求类型外，直接返回异常
         */
        private static class RequestMethodFactory{
            public static HttpRequestBase getRequest(RequestType type){
                switch (type){
                    case GET:return new HttpGet();
                    case PUT:return new HttpPut();
                    case POST:return new HttpPost();
                    case DELETE:return new HttpDelete();
                    default: throw new RuntimeException("can't parse request type,type=" + type);
                }
            }
        }

        /**
         * 真正的请求过程
         * @param uri 请求的uri
         * @param headerParams 请求头参数
         * @param requestType 请求类型
         * @return
         */
        private static HttpRequestResult request(URI uri, Map<String, String> headerParams, RequestType requestType)
        {
            HttpRequestResult result = new HttpRequestResult();
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = null;

            String strResult = null;
            try
            {
                HttpRequestBase request = RequestMethodFactory.getRequest(requestType);
                request.setConfig(requestConfig());
                request.setURI(uri);
                if(headerParams != null && headerParams.size() > 0)
                {
                    String v = "";
                    for(Map.Entry<String, String> entry : headerParams.entrySet())
                    {
                        Object o = entry.getValue();
                        if (o != null){
                            v = String.valueOf(o);
                            request.setHeader(entry.getKey(), v);
                        }
                    }
                }
                response = client.execute(request);
                HttpEntity httpEntity= response.getEntity();
                strResult = EntityUtils.toString(httpEntity);
                result.setReturnValue(strResult);
                result.setStatus(response.getStatusLine().getStatusCode());

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (response != null) response.close();
                    if (client != null) client.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }

            return result;
        }

        /**
         * 真正的请求过程
         * @param url 请求的路径
         * @param params 需要传递的参数
         * @param headerParams 请求头参数
         * @param requestType 请求类型
         * @return
         */
        private static HttpRequestResult request(String url, Map<String, String> params, Map<String, String> headerParams, RequestType requestType)
        {

            HttpRequestResult result = new HttpRequestResult();
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = null;

            String strResult = null;
            try
            {
                HttpRequestBase request = RequestMethodFactory.getRequest(requestType);
                request.setConfig(HttpUtil.requestConfig());

                URIBuilder builder = new URIBuilder(url);
                if(params != null && params.size() > 0)
                {
                    String v = "";
                    for(Map.Entry<String, String> entry : params.entrySet())
                    {
                        Object o = entry.getValue();
                        if (o != null && !o.equals("null")){
                            v = String.valueOf(o);
                            builder.addParameter(entry.getKey(), v);
                        }
                    }
                }
                if(headerParams != null && headerParams.size() > 0)
                {
                    for(Map.Entry<String, String> entry : headerParams.entrySet())
                    {
                        Object o = entry.getValue();
                        String v = "";
                        if (o != null && !o.equals("null")){
                            v = String.valueOf(o);
                            request.setHeader(entry.getKey(), v);
                        }
                    }
                }
                request.setURI(builder.build());

                response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                strResult = EntityUtils.toString(entity, "utf-8");
                result.setReturnValue(strResult);
                result.setStatus(statusCode);

            } catch (Exception e)
            {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (response != null) response.close();
                    if (client != null) client.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
            return result;
        }



        /**
         * get请求
         * @param uri 请求的uri，此处的uri已经包括了所有的请求体参数
         * @param headerParams 请求头参数，可以为null（以下无说明都为null）
         * @return
         */
        public static HttpRequestResult doGet(URI uri, Map<String, String> headerParams)
        {
            return request(uri, headerParams, RequestType.GET);
        }

        /**
         * get请求
         * @param url 请求路径
         * @param params 请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doGet(String url, Map<String, String> params, Map<String, String> headerParams)
        {
            return request(url, params, headerParams, RequestType.GET);
        }

        /**
         * post请求
         * @param uri 请求uri，此处的uri已经包括了所有的请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doPost(URI uri, Map<String, String> headerParams)
        {
            return request(uri, headerParams, RequestType.POST);
        }

        public static HttpRequestResult doPost(String url,Map<String,String> params,Map<String,String> headerParams){

            return doPost(url, params, headerParams, "utf-8");
        }


        /**
         * 新的post方法，解决了中文乱码的问题
         * @param url
         * @param params
         * @param
         * \headerParams
         * @return
         */
        public static HttpRequestResult doPost(String url,Map<String,String> params,Map<String,String> headerParams,String encodeName){

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(HttpUtil.requestConfig());
            CloseableHttpClient client = HttpClients.createDefault();
            HttpRequestResult result = new HttpRequestResult();

            if (headerParams != null){
                for(Map.Entry<String, String> entry : headerParams.entrySet())
                {
                    Object o = entry.getValue();
                    if(entry.getKey() != null && o != null && !"null".equalsIgnoreCase(entry.getKey()) && !"null".equals(o)) {
                        httpPost.setHeader(entry.getKey(), String.valueOf(o));
                    }
                }
            }

            List<NameValuePair> pairs = new ArrayList<>();
            BasicNameValuePair pair = null;
            for(Map.Entry<String, String> entry : params.entrySet())
            {
                Object o = entry.getValue();
                if(entry.getKey() != null && o != null && !"null".equalsIgnoreCase(entry.getKey()) && !"null".equals(o)){
                    pair = new BasicNameValuePair(entry.getKey(),String.valueOf(o));
                    pairs.add(pair);
                }

            }
            CloseableHttpResponse response = null;
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs,encodeName));
                response = client.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();

                String strResult = EntityUtils.toString(entity, "utf-8");

                result.setReturnValue(strResult);
                result.setStatus(statusCode);
                return result;
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (response != null) response.close();
                    if (client != null) client.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }


        /**
         * 传参数的方式不一样，该方法将参数转成json放在body中，与formData传参不一样
         * @param url 请求路径
         * @param params 请求参数,map对象，value为Object是因为需要传对象，若为String，最后接口的字段类型也为String
         * @param headerParams 请求头参数
         * @param encodeName 编码
         * @return 请求后返回的数据
         */
        public static HttpRequestResult doBodyPost(String url, Map<String,Object> params, Map<String,String> headerParams, String encodeName){

            //与其他同样的创造方法
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(HttpUtil.requestConfig());
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setContentEncoding("utf-8");
            CloseableHttpResponse response = null;

            //添加请求头参数
            if (headerParams != null && !headerParams.isEmpty()){
                for (Map.Entry<String,String> entry : headerParams.entrySet()){
                    httpPost.setHeader(entry.getKey(),entry.getValue());
                }
            }

            //设置请求体参数，将参数转成json形式传送，可解决中文乱码问题
            httpPost.setEntity(new StringEntity(JSON.toJSONString(params),Charset.forName(encodeName)));
            try {
                response = httpClient.execute(httpPost);
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String returnValue = EntityUtils.toString(entity,"utf-8");
                HttpRequestResult result = new HttpRequestResult();
                result.setStatus(status);
                result.setReturnValue(returnValue);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                try{
                    if (response != null) {
                        response.close();
                    }
                    if (httpClient != null) {
                        httpClient.close();
                    }
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * post请求，目前已弃用
         * @param url 请求路径
         * @param params 请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        @Deprecated
        public static HttpRequestResult post(String url, Map<String, String> params, Map<String, String> headerParams)
        {
            HttpRequestResult result = new HttpRequestResult();
            CloseableHttpResponse response = null;
            CloseableHttpClient client = null;
            String strResult = null;
            try
            {
                HttpPost httpPost = new HttpPost(url);
                client = HttpClients.createDefault();

                EntityBuilder entityBuilder = EntityBuilder.create();
                entityBuilder.setContentEncoding("utf-8");
                List<NameValuePair> pairs = new ArrayList<>();
                BasicNameValuePair pair = null;
                for(Map.Entry<String, String> entry : params.entrySet())
                {
                    Object o = entry.getValue();
                    if(entry.getKey() != null && o != null && !"null".equalsIgnoreCase(entry.getKey()) && !"null".equals(o)){
                        pair = new BasicNameValuePair(entry.getKey(),String.valueOf(o));
                        pairs.add(pair);
                    }

                }
                entityBuilder.setParameters(pairs);
                if(headerParams != null && headerParams.size() > 0)
                {
                    for(Map.Entry<String, String> entry : headerParams.entrySet())
                    {
                        Object o = entry.getValue();
                        if(entry.getKey() != null && o != null && !"null".equalsIgnoreCase(entry.getKey()) && !"null".equals(o)) {
                            httpPost.setHeader(entry.getKey(), String.valueOf(o));
                        }
                    }
                }
                httpPost.setEntity(entityBuilder.build());
                response = client.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                strResult = EntityUtils.toString(entity, "utf-8");
                result.setReturnValue(strResult);
                result.setStatus(statusCode);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (response != null) response.close();
                    if (client != null) client.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
            return result;
        }

        /**
         * delete请求
         * @param uri 请求的uri，此处的uri已经包括了所有的请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doDelete(URI uri, Map<String, String> headerParams)
        {
            return request(uri, headerParams, RequestType.DELETE);
        }

        /**
         * delete请求
         * @param url 请求路径
         * @param params 请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doDelete(String url, Map<String, String> params, Map<String, String> headerParams)
        {
            return request(url, params, headerParams, RequestType.DELETE);
        }

        /**
         * put请求
         * @param uri 请求的uri，此处的uri已经包括了所有的请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doPut(URI uri, Map<String, String> headerParams)
        {
            return request(uri, headerParams, RequestType.PUT);
        }

        /**
         * put请求
         * @param url 请求路径
         * @param params 请求体参数
         * @param headerParams 请求头参数
         * @return
         */
        public static HttpRequestResult doPut(String url, Map<String, String> params, Map<String, String> headerParams)
        {
            return request(url, params, headerParams, RequestType.PUT);
        }

        /**
         * 上传文件请求
         * @param uploadurl 上传的路径
         * @param file 上传的文件
         * @param key 接口接收文件的参数
         * @return
         */
        public static String doMultiPost(String uploadurl, File file, String key)
        {

            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost(uploadurl);
                FileBody bin = new FileBody(file);
                HttpEntity reqEntity = MultipartEntityBuilder.create().addPart(key, bin).build();
                post.setEntity(reqEntity);
                String responseContent = null;
                CloseableHttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200)
                {
                    HttpEntity entity = response.getEntity();
                    responseContent = EntityUtils.toString(entity, "UTF-8");
                }
                return responseContent;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 上传文件的请求
         * @param uploadurl 上传路径
         * @param params 请求体参数
         * @param headerParams 请求头参数
         * @param files 包含文件以及对应的接口接收文件的参数，可多个
         * @return
         */
        public static HttpRequestResult doMultiPost(String uploadurl, Map<String, String> params, Map<String, String> headerParams, Map<String, File> files)
        {

            HttpRequestResult result = new HttpRequestResult();
            CloseableHttpClient client = null;
            try {
                client = HttpClients.createDefault();
                HttpPost post = new HttpPost(uploadurl);
                post.setConfig(HttpUtil.requestConfig());
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(Charset.forName("utf-8"));
                if (params != null && params.size() > 0) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                    }
                }

                if(headerParams != null && headerParams.size() > 0)
                {
                    for(Map.Entry<String, String> entry : headerParams.entrySet())
                    {
                        post.setHeader(entry.getKey(), entry.getValue());
                    }
                }

                if(files != null && files.size() > 0) {
                    Set<Map.Entry<String, File>> entries = files.entrySet();
                    for (Map.Entry<String, File> entry : entries) {
                        builder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.DEFAULT_BINARY.withCharset("utf-8"), entry.getValue().getName());
                    }
                }
                builder.setLaxMode();
                post.setEntity(builder.build());
                String responseContent = null;
                CloseableHttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
                result.setReturnValue(responseContent);
                result.setStatus(response.getStatusLine().getStatusCode());
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally
            {
                if(client != null)
                    try {
                        client.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

            }
        }

        /**
         * 上传文件接口，由于MultipartFile不能转成File类型，此处使用byte[]上传
         * @param uploadUrl 上传的路径
         * @param params 求情体参数
         * @param headers
         * @param files 上传的文件
         * @return
         */
        public static HttpRequestResult doMultiPost(String uploadUrl, Map<String, String> params, List<Header> headers, Map<String, MultipartFile> files){

            HttpRequestResult result = new HttpRequestResult();
            CloseableHttpClient client = null;
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(uploadUrl);
            post.setConfig(HttpUtil.requestConfig());
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }

            try{
                //获取multipartFile文件的字节，用于上传，接口处使用MultipartFile同样可以接收该文件
                if(files != null && files.size() > 0) {
                    Set<Map.Entry<String, MultipartFile>> entries = files.entrySet();
                    MultipartFile file = null;
                    for (Map.Entry<String, MultipartFile> entry : entries) {
                        file = entry.getValue();
                        builder.addBinaryBody(entry.getKey(), file.getBytes(), ContentType.DEFAULT_BINARY.withCharset("utf-8"), file.getOriginalFilename());
                    }
                }

                if(headers != null && headers.size() > 0)
                {
                    for(Header header : headers) {
                        post.setHeader(header);
                    }
                }

                builder.setLaxMode();
                post.setEntity(builder.build());
                String responseContent = null;
                CloseableHttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
                result.setReturnValue(responseContent);
                result.setStatus(response.getStatusLine().getStatusCode());
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                if(client != null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }


        public static RequestConfig requestConfig(int socketTimeout, int connectTimeout, int connectionRequestTimeout){
            return RequestConfig
                    .custom()
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();
        }

        public static RequestConfig requestConfig(){
            return requestConfig(30000,10000,30000);
        }

        /**
         * 检测传进来的url是否合法
         * @param url 待检测的url
         * @return 传进来的url是否合法
         */
        public static boolean legalUrl(String url){
            URI uri = null;
            if (url == null || url.length() == 0){
                return false;
            }
            try {
                uri = URI.create(url);
            } catch (Exception e){
                return false;
            }
            if (uri.getHost() == null){
                return false;
            }
            return true;
        }

        /**
         * 请求url，检测该url是否可用
         * @param url 需要检测的url
         * @return 传进来的url是否可用
         */
        public static boolean validUrl(String url){
            if (url == null || url.length() == 0){
                return false;
            }

            URL url1 = null;
            try {
                url1 = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }
            int reconnect = 0;
            HttpURLConnection connection = null;
            while (reconnect < 5){
                try {
                    connection = (HttpURLConnection) url1.openConnection();
                    if (connection.getResponseCode() == 200){
                        return true;
                    } else {
                        reconnect ++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    reconnect ++;
                    continue;
                } finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
                url1 = null;
            }

            return false;
        }





        /**
         * 请求的四种类型
         */
        private enum RequestType{
            GET,POST,PUT,DELETE;
        }

        //请求返回的结构
        public static class HttpRequestResult {
            private String returnValue;
            private int status;

            public HttpRequestResult()
            {

            }
            public HttpRequestResult(String returnValue, int status)
            {
                this.returnValue = returnValue;
                this.status = status;
            }
            public String getReturnValue()
            {
                return returnValue;
            }
            public void setReturnValue(String returnValue)
            {
                this.returnValue = returnValue;
            }
            public int getStatus()
            {
                return status;
            }
            public void setStatus(int status)
            {
                this.status = status;
            }
        }

    }

