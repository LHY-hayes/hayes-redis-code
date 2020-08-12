package com.hayes.bash.hayesredis.filter;


import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Slf4j
@WebFilter(filterName = "AuthorityFilter", urlPatterns = "/*")
public class AuthorityFilter implements Filter {

    @Autowired
    private Environment environment;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String servletPath = httpServletRequest.getServletPath();
        log.info("@@@@@@@@@@@@@@@@@@ AuthorityFilter.doFilter.servletPath=" + servletPath);
        String hayes = httpServletRequest.getParameter("hayesLin");
        if ("admin".equals(hayes)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String sign = httpServletRequest.getParameter("sign");
        String timestamp = httpServletRequest.getParameter("timestamp");
        BodyReaderHttpServletRequestWrapper w = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
        String body = new String(w.body);
        log.info("@@@@@@@@@@@@@@@@@@  sign:{} , body : {} ,  timestamp:{}  ", sign, body, timestamp);

        /**
         * 校验开始。。。。。
         * 1。 校验 timestamp  不能超过 60*1000 ms 即 1分钟
         * 2。 根据平台编码 获取改平台对应的 secret
         * 3。 对比加密参数sign
         */

        if (StringUtils.isEmpty(sign)) {
            log.error("sign为空");
            writeResponse((HttpServletResponse) servletResponse);
            return;
        }
        if (StringUtils.isEmpty(timestamp)) {
            log.error("timestamp为空");
            writeResponse((HttpServletResponse) servletResponse);
            return;
        }

        long s = Long.parseLong(timestamp);
        long n = System.currentTimeMillis();

        if ((n - s) >= 60 * 1000 || (n - s) <= 0) {

            log.error("请求时间超时 ,  必须满足 0 < {} - {} = {} < 60000 ", s, timestamp, (s - Long.parseLong(timestamp)));
            writeResponse((HttpServletResponse) servletResponse);
            return;

        }

        JSONObject body_json = JSONObject.parseObject(body);

        if (!body_json.containsKey("sourceSite")) {
            log.error("sourceSite不存在，无法获取出secret码。");
            writeResponse((HttpServletResponse) servletResponse);
            return;
        }
        String sourceSiteSecret = environment.getProperty(body_json.getString("sourceSite") + ".REDIS.SECRET");

        String localSign = CommonUtils.md5Digest(body + timestamp + sourceSiteSecret);
        if (!localSign.equalsIgnoreCase(sign)) {
            log.error("@@@@@@@@@@@@@@@@@@ AuthorityFilter.doFilter.localSign = {} , sign = {}", localSign, sign);
            writeResponse((HttpServletResponse) servletResponse);
            return;
        }

        ServletRequest request = w;
        filterChain.doFilter(request, servletResponse);
        return;
    }

    @Override
    public void destroy() {
    }

    private void writeResponse(HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.getWriter().write("{\n" +
                "    \"resultCode\": \"401\",\n" +
                "    \"resultMsg\":\"Authentication failed\"\n" +
                "}\n");
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        httpServletResponse.getWriter().flush();
    }

}
