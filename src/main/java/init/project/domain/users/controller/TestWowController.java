package init.project.domain.users.controller;

import init.project.domain.users.model.TestRQ;
import init.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/damn")
public class TestWowController {

    @PostMapping("/wow")
    public ResponseEntity<?> test(@RequestBody TestRQ request) {
        log.info("wow1: {}", request.getWow1());
        log.info("wow2: {}", request.getWow2());
        log.info("wow3: {}", request.getWow3());

//        return ApiResponse.success(Map.of(
//                "test1", "test1",
//                "test2", "test2",
//                "test3", "test3"
//        ));
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "test");
    }

    @GetMapping("/wow")
    public ResponseEntity<?> getWow(@ModelAttribute TestRQ request) {
        log.info("wow1: {}", request.getWow1());
        log.info("wow2: {}", request.getWow2());
        log.info("wow3: {}", request.getWow3());

        return ApiResponse.success(Map.of(
                "test1", "test1",
                "test2", "test2",
                "test3", "test3"
        ));
    }

}