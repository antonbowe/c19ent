package de.antonbowe.c19ent.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class TestController {

    public TestController() {
    System.out.println("Controller created");
    }

    @GetMapping("/a")
    private String test() {
        return "Hello World";
    }

}
