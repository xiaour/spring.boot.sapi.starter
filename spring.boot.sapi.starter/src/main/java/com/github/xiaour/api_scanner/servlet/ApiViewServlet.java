package com.github.xiaour.api_scanner.servlet;

import com.github.xiaour.api_scanner.config.ApiServerAutoConfigure;
import com.github.xiaour.api_scanner.logging.Log;
import com.github.xiaour.api_scanner.logging.LogFactory;
import com.github.xiaour.api_scanner.util.Utils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "ApiViewServlet", urlPatterns = {"/sapi/*"})
@ConditionalOnProperty(name = "spring.sapi.enable", havingValue = "true", matchIfMissing = true)
public class ApiViewServlet extends HttpServlet {

    private final static Log LOG = LogFactory.getLog(ApiViewServlet.class);

    protected final String resourcePath;

    public ApiViewServlet(){
        this.resourcePath = "support/http";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式
        resp.setCharacterEncoding("UTF-8");//setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题
        String url=req.getRequestURI();

        if(url.equals(ApiServerAutoConfigure.getContextPath()+"/sapi/list".replaceAll("//","/"))) {
            list(req, resp);
        }else {
            index(req,resp);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = "/list.html";

        try {
            returnResourceFile(path, uri, response);
        } catch (ServletException e) {
            LOG.error("Sapi init exception:",e);
        }
    }

    private void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if ("/".equals(path)||"".equals(path)) {
            path="/index.html";
        }

        try {
            returnResourceFile(path, uri, response);
        } catch (ServletException e) {
            LOG.error("Sapi init exception:",e);
        }
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException,
            IOException {

        String filePath = getFilePath(fileName);

        if (filePath.endsWith(".html")) {
            response.setContentType("text/html; charset=utf-8");
        }
        if (fileName.endsWith(".jpg")) {
            byte[] bytes = Utils.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }

            return;
        }

        String text = Utils.readFromResource(filePath);
        if (text == null) {
            response.sendRedirect(uri + "/index.html");
            return;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");

        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

    protected String getFilePath(String fileName) {
        return resourcePath + fileName;
    }
}
