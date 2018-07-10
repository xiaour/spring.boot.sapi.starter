package com.github.xiaour.api_scanner.servlet;

import com.github.xiaour.api_scanner.dto.ApiInfo;
import com.github.xiaour.api_scanner.filter.SapiFactoryAutoConfigure;
import com.github.xiaour.api_scanner.util.SapiJsonUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/6/4 10:07
 */
@WebServlet(name = "ApiJsonServlet", urlPatterns = {"/apiList"})
public class ApiJsonServlet extends HttpServlet {

    private static Integer pageSize=10;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式
        response.setCharacterEncoding("UTF-8");//setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题
        Integer pageNum;
        int totalPage=-1;

        Map<String,Object> map= new HashMap<>();

        if(StringUtils.hasText(request.getParameter("pageNum"))){
            pageNum=Integer.parseInt(request.getParameter("pageNum"));
            totalPage=getPageCount(pageSize,SapiFactoryAutoConfigure.simpleApiList.size());
            map.put("apiList",getListByPage(pageSize,pageNum,SapiFactoryAutoConfigure.simpleApiList));
        }else{
            map.put("apiList",SapiFactoryAutoConfigure.simpleApiList);
        }
        map.put("totalPage",totalPage);

        PrintWriter out=response.getWriter() ;
        out.write(SapiJsonUtil.mapJsonUtil(map));
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req,resp);
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

        if(pageSize%total>0){

            pageCount=total/pageSize+1;

        }else{
            pageCount=total/pageSize;
        }

        return pageCount;
    }




}
