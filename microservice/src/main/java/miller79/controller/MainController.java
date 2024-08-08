package miller79.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/web/webflux/controller.html">Annotated
 *      Controllers</a>
 */
@RestController
public class MainController {
    @GetMapping
    public String hello() {
        return "Hello World";
    }
}
