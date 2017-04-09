package taobao.autosell.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import taobao.autosell.entity.AuthUser;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.rest.BotResult;
import taobao.autosell.entity.rest.CardResult;
import taobao.autosell.entity.rest.JsonResult;
import taobao.autosell.entity.rest.Result;
import taobao.autosell.repository.AuthUserRepository;
import taobao.autosell.repository.OrderPushRepository;
import taobao.autosell.service.ManagementService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    @Autowired
    private HttpSession session;

    @Value("${manager.key}")
    private String key;
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
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void default1(HttpServletResponse response) throws IOException {
        response.sendRedirect("manage.html");
    }
    @RequestMapping(value = "verify", method = RequestMethod.GET)
    public void verify(String key,HttpServletResponse response) throws IOException {
        if (key.trim().equals(this.key)) {
            session.setAttribute("admin", key);
        }
        response.sendRedirect("manage.html");
    }
    @RequestMapping(value = "repository",method = RequestMethod.GET)
    public @ResponseBody
    JsonResult repository(Integer page,String key){
        if (verify()) {
            return managementService.getRepository(page);
        }else {
            return new JsonResult(false, "未验证用户..","");
        }
    }
    private boolean verify(){
        Object key = session.getAttribute("admin");
        if (key == null || !key.equals(this.key)){
            return false;
        }else {
            return true;
        }
    }

    @RequestMapping(value = "update",method = RequestMethod.GET)
    public void update(HttpServletResponse response) throws IOException {
        if (verify()) {
            managementService.updateNumber();
        }else {
            response.sendRedirect("manage.html");
        }
    }
    @RequestMapping(value = "update/single",method = RequestMethod.GET)
    public void updateSingle(String name,HttpServletResponse response) throws IOException {
        if (verify()){
            managementService.updateNumber(name);
        }else {
            response.sendRedirect("manage.html");
        }
    }


    @RequestMapping(value = "newStorage",method = RequestMethod.POST)
    public void newStorage(MultipartFile file,HttpServletResponse response) throws IOException {
        if (verify()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line = reader.readLine();
            String html = "";
            while (line != null) {
                html += line;
                line = reader.readLine();
            }
            managementService.newStorage(html);
        }
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
    public void newPair(String pair,String title,String key,HttpServletResponse response) throws IOException {
        if (verify()) {
            managementService.savePair(title, pair);
        }else {
            response.sendRedirect("manage.html");
        }

    }
    @RequestMapping(value = "order/detail",method = RequestMethod.GET)
    public @ResponseBody JsonResult orderDetail(String order,HttpServletResponse response) throws IOException {
        if (verify()) {
            return managementService.orderDetail(order);
        }else {
            JsonResult jsonResult = new JsonResult(false,"未验证的用户..","");
            jsonResult.setTotal(3);
            return jsonResult;
        }
    }
    @RequestMapping(value = "order/save",method = RequestMethod.GET)
    public @ResponseBody JsonResult orderSave(String orderId,String steamId,String status,HttpServletResponse response){
        if (verify()){
            return managementService.orderSave(orderId, steamId, status);
        }else {
            JsonResult jsonResult = new JsonResult(false,"未验证的用户..","");
            jsonResult.setTotal(3);
            return jsonResult;
        }
    }
    @RequestMapping(value = "tradeoffer/receipt",method = RequestMethod.POST)
    public void orderSave(String receipt){
        managementService.newStorage(receipt);
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
