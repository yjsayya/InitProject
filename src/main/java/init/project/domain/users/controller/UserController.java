package init.project.domain.users.controller;

import init.project.domain.users.model.request.UserJoinRQ;
import init.project.domain.users.service.UserService;
import init.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserJoinRQ userJoinRQ) {
        userService.join(userJoinRQ);
        return ApiResponse.success();
    }

}