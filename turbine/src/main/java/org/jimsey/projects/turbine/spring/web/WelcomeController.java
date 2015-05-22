package org.jimsey.projects.turbine.spring.web;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

  @RequestMapping("/welcome")
  public String welcome(Map<String, Object> model) {
    model.put("time", new Date());
    model.put("message", "Hello from Freemarker!");
    return "welcome";
  }

}
