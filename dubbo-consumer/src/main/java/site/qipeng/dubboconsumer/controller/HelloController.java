package site.qipeng.dubboconsumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.qipeng.dubboapi.HelloService;

@RestController
public class HelloController {

    @Reference
    private HelloService helloService;

    @RequestMapping("/hello")
    public String hello(@RequestParam("name") String name){

        return helloService.seyHello(name);
    }

}
