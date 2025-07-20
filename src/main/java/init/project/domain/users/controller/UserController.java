package init.project.domain.users.controller;

import init.project.domain.users.model.request.UserJoinRQ;
import init.project.domain.users.model.response.UserAccountDetailRS;
import init.project.domain.users.service.UserService;
import init.project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/detail/{userId}")
    public ResponseEntity<?> fetchUserDetail(@PathVariable Long userId) {
        UserAccountDetailRS response = userService.fetchUserAccountDetail(userId);
        return ApiResponse.success(response);
    }

}