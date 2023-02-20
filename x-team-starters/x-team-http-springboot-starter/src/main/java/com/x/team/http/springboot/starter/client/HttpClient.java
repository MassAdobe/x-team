package com.x.team.http.springboot.starter.client;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.HttpConstants;
import com.x.team.http.springboot.starter.dto.PostWithHeaderDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 描述：http-client
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:29
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class HttpClient {

    private static final MediaType JSON = MediaType.parse(HttpConstants.APPLICATION_JSON_UTF8);
    private static final MediaType XML = MediaType.parse(HttpConstants.APPLICATION_XML_UTF8);

    @Autowired
    private OkHttpClient okHttpClient;

    /**
     * 统一头处理
     */
    private Request.Builder returnRequestBuilder() {
        return new Request.Builder().header("Connection", "close");
    }

    /**
     * get request
     *
     * @param url     request url path
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string 返回值
     */
    public String doGet(String url, Map<String, String> headers) {
        return executeGet(url, headers);
    }

    /**
     * get request
     *
     * @param url     request url path
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string 返回值
     */
    public PostWithHeaderDto doGetReturnHeaders(String url, Map<String, String> headers) {
        return executeGetReturnHeaders(url, headers);
    }

    /**
     * get 请求
     *
     * @param url     request url path
     * @param params  request params map
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return executeGet(url, params, headers);
    }

    /**
     * get request
     *
     * @param url     request url path
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string
     */
    private String executeGet(String url, Map<String, String> headers) {
        Request.Builder builder = this.returnRequestBuilder();
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).build();
        log.info("【OK-HTTP】：get request，do get request and url[{}]", url);
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute.getAns();
        }
        return CommonConstants.EMPTY;
    }

    /**
     * get request
     *
     * @param url     request url path
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string
     */
    private PostWithHeaderDto executeGetReturnHeaders(String url, Map<String, String> headers) {
        Request.Builder builder = this.returnRequestBuilder();
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).build();
        log.info("【OK-HTTP】：get request，do get request and url[{}]", url);
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute;
        }
        return null;
    }

    /**
     * get request
     *
     * @param url     request url path
     * @param params  request params map
     * @param headers request headers {k1, v1 k2, v2, ...}
     * @return string
     */
    public String executeGet(String url, Map<String, String> params, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder(url);
        if (!params.isEmpty()) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(sb.toString()).build();
        log.info("【OK-HTTP】：get request，do get request and url[{}]", sb);
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute.getAns();
        }
        return CommonConstants.EMPTY;
    }

    /**
     * post request
     *
     * @param url    request url address
     * @param params request headers map
     * @return string
     */
    public String doPostJson(String url, String params, Map<String, String> headers) {
        log.info("【OK-HTTP】：post 请求，do post request and url[{}]", url);
        return executePost(url, params, JSON, headers);
    }

    /**
     * post request
     *
     * @param url    request url address
     * @param params request headers map
     * @return PostWithHeaderDto
     */
    public PostWithHeaderDto doPostJsonReturnHeaders(String url, String params, Map<String, String> headers) {
        log.info("【OK-HTTP】：post 请求，do post request and url[{}]", url);
        return executePostReturnHeaders(url, params, JSON, headers);
    }

    /**
     * post request
     *
     * @param url    request url address
     * @param params request headers map
     * @return string
     */
    public PostWithHeaderDto doPostJsonWithHeaders(String url, String params, Map<String, String> headers) {
        log.info("【OK-HTTP】：post 请求，do post request and url[{}]", url);
        return executePost(url, params, headers);
    }

    /**
     * put request
     *
     * @param url    request url address
     * @param params request headers map
     * @return string
     */
    public String doPutJson(String url, String params, Map<String, String> headers) {
        log.info("【OK-HTTP】：put 请求，do put request and url[{}]", url);
        return executePut(url, params, headers);
    }

    /**
     * delete request
     *
     * @param url    request url address
     * @param params request headers map
     * @return string
     */
    public String doDeleteJson(String url, String params, Map<String, String> headers) {
        log.info("【OK-HTTP】：delete 请求，do delete request and url[{}]", url);
        return executeDelete(url, params, headers);
    }

    /**
     * post request, request data as xml string
     *
     * @param url request url address
     * @param xml request data, xml string
     * @return string
     */
    public String doPostXml(String url, String xml, Map<String, String> headers) {
        log.info("【OK-HTTP】：post request，do post request and url[{}]", url);
        return executePost(url, xml, XML, headers);
    }

    @SuppressWarnings("all")
    public String executePost(String url, String data, MediaType contentType, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(contentType, StringUtils.isEmpty(data) ? CommonConstants.EMPTY : data);
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).post(requestBody).header("Connection", "close").build();
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute.getAns();
        }
        return null;
    }

    @SuppressWarnings("all")
    public PostWithHeaderDto executePostReturnHeaders(String url, String data, MediaType contentType, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(contentType, StringUtils.isEmpty(data) ? CommonConstants.EMPTY : data);
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).post(requestBody).header("Connection", "close").build();
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute;
        }
        return null;
    }

    @SuppressWarnings("all")
    public PostWithHeaderDto executePost(String url, String data, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(JSON, data);
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).post(requestBody).build();
        return execute(request);
    }


    @SuppressWarnings("all")
    public String executePut(String url, String data, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(JSON, data);
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).put(requestBody).build();
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute.getAns();
        }
        return null;
    }

    @SuppressWarnings("all")
    public String executeDelete(String url, String data, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(JSON, data);
        Request.Builder builder = new Request.Builder().header("Connection", "close");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).delete(requestBody).build();
        PostWithHeaderDto execute = execute(request);
        if (Objects.nonNull(execute)) {
            return execute.getAns();
        }
        return null;
    }

    private PostWithHeaderDto execute(Request request) {
        try (Response response = this.okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("【执行HTTP调用】: 执行成功, response: {}", response);
            } else {
                log.error("【执行HTTP调用】: 执行失败, response: {}", response);
            }
            if (Objects.nonNull(response.body())) {
                return PostWithHeaderDto.builder()
                        .ans(response.body().string())
                        .headers(response.headers())
                        .build();
            }
        } catch (Exception e) {
            log.error("【执行HTTP调用】: 错误: 错误原因: {}", e.getMessage());
            return PostWithHeaderDto.builder()
                    .headers(null)
                    .ans(CommonConstants.EMPTY)
                    .build();
        }
        return null;
    }
}
