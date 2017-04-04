package taobao.autosell.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import taobao.autosell.entity.AuthUser;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.rest.CardResult;
import taobao.autosell.entity.rest.JsonResult;
import taobao.autosell.entity.rest.Result;
import taobao.autosell.repository.AuthUserRepository;
import taobao.autosell.repository.OrderPushRepository;
import taobao.autosell.service.ManagementService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by asus on 2016/11/12.
 */
@Controller
@RequestMapping("/")
public class GetItem {
    @Autowired
    private ManagementService managementService;
    @Autowired
    private OrderPushRepository orderPushRepository;

    @Autowired
    private AuthUserRepository authUserRepository;
    @RequestMapping(value = "/getOrder", method = RequestMethod.GET)
    public String index(){
        return "1";
    }
//    @RequestMapping(value = "index", method = RequestMethod.POST)
//    public void index1(){
//    }

//    @RequestMapping(value = "/manage",method = RequestMethod.GET)
//    public String repository(){
//        String  a = "";
//        a.length();
//        return "manage11";
//    }

    @RequestMapping(value = "repository",method = RequestMethod.GET)
    public @ResponseBody
    JsonResult repository(Integer page,String key){
        if (key.trim().equals("4fead7f1-7d6c-4d1e-8cf7-cbcfbefa0d97")) {
            return managementService.getRepository(page);
        }else {
            return new JsonResult(false,"密钥错误！","");
        }
    }

    @RequestMapping(value = "update",method = RequestMethod.GET)
    public void update(String key){
        if (key.trim().equals("4fead7f1-7d6c-4d1e-8cf7-cbcfbefa0d97")) {
            managementService.updateNumber();
        }
    }


    @RequestMapping(value = "newStorage",method = RequestMethod.POST)
    public void newStorage(MultipartFile file,HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line = reader.readLine();
        String html = "";
        while (line != null){
            html += line;
            line = reader.readLine();
        }
        managementService.newStorage(html);
        response.sendRedirect("manage.html");
    }

    @RequestMapping(value = "cardOrder",method = RequestMethod.GET)
    public @ResponseBody CardResult newStorage(String oid) throws IOException {
        Float payment = managementService.cardOrder(oid);
        if (payment != null && payment != 0f){
            return new CardResult(true,"",payment);
        }else {
            return new CardResult(false,"",0f);
        }
    }
    @RequestMapping(value = "newPair",method = RequestMethod.GET)
    public void newPair(String pair,String title,String key){
        if (key.trim().equals("4fead7f1-7d6c-4d1e-8cf7-cbcfbefa0d97")) {
            managementService.savePair(title, pair);
        }

    }
    @RequestMapping(value = "order/detail",method = RequestMethod.GET)
    public @ResponseBody JsonResult orderDetail(String order){
        return managementService.orderDetail(order);
    }
    @RequestMapping(value = "order/save",method = RequestMethod.GET)
    public @ResponseBody Result orderSave(String orderId,String steamId,String status){
        return managementService.orderSave(orderId, steamId, status);
    }
    @RequestMapping(value = "auth",method = RequestMethod.GET)
    public @ResponseBody Result auth(String id){
        AuthUser authUser = authUserRepository.findOne(id);
        if (authUser == null || authUser.getTime() < System.currentTimeMillis()){
            return new Result(false,"已到期，请续费！","");
        }else {
            return new Result(true,"success","");
        }
    }
}
