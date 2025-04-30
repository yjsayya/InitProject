package init.project.domain.test;

import init.project.global.properties.UsersProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final UsersProperty usersProperty;

    @PostMapping
    public void test() {
        log.info("name: {}", usersProperty.getInfo().getName());
        log.info("age: {}", usersProperty.getInfo().getAge());
        log.info("processName: {}", usersProperty.getProcessName());
    }

}