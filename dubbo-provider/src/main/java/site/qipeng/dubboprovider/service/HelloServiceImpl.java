package site.qipeng.dubboprovider.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import site.qipeng.dubboapi.HelloService;

@Service(interfaceClass = HelloService.class)
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public String seyHello(String name) {
        return "Hello " + name + " , this is provider";
    }
}
