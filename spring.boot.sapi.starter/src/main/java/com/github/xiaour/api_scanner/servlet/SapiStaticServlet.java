package com.github.xiaour.api_scanner.servlet;

import com.github.xiaour.api_scanner.logging.Log;
import com.github.xiaour.api_scanner.logging.LogFactory;
import com.github.xiaour.api_scanner.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "sapistatic", urlPatterns = {"/sapistatic"})
public class SapiStaticServlet extends HttpServlet {

    private final static Log LOG = LogFactory.getLog(SapiStaticServlet.class);


    protected final String resourcePath;


    public SapiStaticServlet(){
        this.resourcePath = "support/http";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }

        String resourceFile=request.getParameter("s");
        String uri = contextPath + servletPath;
        //String path = requestURI.substring(contextPath.length() + servletPath.length());

        try {
            returnResourceFile(resourceFile, uri, response);
        } catch (ServletException e) {
            LOG.error("Sapi init exception:",e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req,resp);
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException,
            IOException {

        String filePath = new ApiViewServlet().getFilePath(fileName);

        if (filePath.endsWith(".html")) {
            response.setContentType("text/html; charset=utf-8");
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
}
