package com.github.xiaour.api_scanner.servlet;

import com.github.xiaour.api_scanner.config.ApiServerAutoConfigure;
import com.github.xiaour.api_scanner.config.SapiGroupManager;
import com.github.xiaour.api_scanner.dto.ApiInfo;
import com.github.xiaour.api_scanner.util.SapiJsonUtil;

import javax.el.MethodNotFoundException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.github.xiaour.api_scanner.config.SapiFactoryAutoConfigure;
@WebServlet(name = "ApiJsonServlet", urlPatterns = {"/sapidata/*"})
public class ApiJsonServlet extends HttpServlet {

    private static Integer pageSize=10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式

        resp.setCharacterEncoding("UTF-8");//setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题

        String url=req.getRequestURI();

        if(!ApiServerAutoConfigure.apiRouter.contains(url)){
            throw new MethodNotFoundException();
        }
        if(url.equals(ApiServerAutoConfigure.getContextPath()+"/sapidata/apiList")) {
            getApiList(req, resp);
        }
        if(url.equals(ApiServerAutoConfigure.getContextPath()+"/sapidata/group")){
            getApiGroup(req,resp);
        }
    }

    private void getApiList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer pageNum;

        int totalPage = -1;

        Map<String,Object> map = new HashMap<String,Object>();

        if(request.getParameter("pageNum")!=null){

            pageNum = Integer.parseInt(request.getParameter("pageNum"));

            totalPage = getPageCount(pageSize, SapiFactoryAutoConfigure.simpleApiList.size());

            map.put("apiList",getListByPage(pageSize,pageNum,SapiFactoryAutoConfigure.simpleApiList));
        }else{

            map.put("apiList", SapiFactoryAutoConfigure.simpleApiList);

        }
        map.put("totalPage",totalPage);

        PrintWriter out=response.getWriter() ;

        out.write(SapiJsonUtil.mapJsonUtil(map));

        out.close();
    }

    private void getApiGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,Object> map = new HashMap<String,Object>();

        Set<String> group = SapiGroupManager.getSapiGroup();

        List<ApiInfo> apiList = SapiFactoryAutoConfigure.simpleApiList;

        Map<String,Object> childMap;

        for(String title:group){
            childMap = new HashMap<String, Object>();
            for(ApiInfo apiInfo:apiList){
                if(apiInfo.getGroupTitle().equals(title)){
                    childMap.put(apiInfo.getUrl(),apiInfo);
                }
            }
            map.put(title,childMap);
        }

        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("total",apiList.size());
        resultMap.put("totalModule",group.size());
        resultMap.put("data",map);
        PrintWriter out=response.getWriter() ;
        out.write(SapiJsonUtil.mapJsonUtil(resultMap));
        out.close();
    }


    public static List<ApiInfo> getListByPage(Integer pageSize, Integer pageNum, List<ApiInfo> list){

        int size = list.size();

        int pageCount=getPageCount(pageSize,size);

        int fromIndex = pageSize * (pageNum - 1);

        int toIndex = fromIndex + pageSize;

        if (toIndex >= size) {

            toIndex = size;
        }
        if(pageNum>pageCount+1){
            fromIndex=0;
            toIndex=0;
        }

        if(fromIndex>size){
            getListByPage(pageSize,pageNum-1,list);
        }
        return list.subList(fromIndex, toIndex);
    }


    public  static Integer getPageCount(Integer pageSize,Integer total){
        int pageCount;

        total=total<=0?1:total;

        if(pageSize%total>0){

            pageCount=total/pageSize+1;

        }else{
            pageCount=total/pageSize;
        }

        return pageCount;
    }




}
