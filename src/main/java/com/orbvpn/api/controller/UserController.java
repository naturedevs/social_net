package com.orbvpn.api.controller;

import com.orbvpn.api.domain.dto.BulkUserCreate;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.service.UserService;
import com.orbvpn.api.utils.CSVUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@RestController
public class UserController {

    private final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        SUPPORTED_EXTENSIONS.add("csv");
    }

    @PostMapping("/user/create-bulk")
    @RolesAllowed(ADMIN)
    public ResponseEntity<String> createBulkUsers(@RequestParam("file") MultipartFile multipartFile)
            throws IOException {

        String extension = Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.")[1];
        if(!SUPPORTED_EXTENSIONS.contains(extension.toLowerCase())){
           return new ResponseEntity<>(String.format("Allowed file extensions are : %s", SUPPORTED_EXTENSIONS),HttpStatus.BAD_REQUEST);
        }

        BulkUserCreate usersToCreate = CSVUtils.csvToUsers(multipartFile.getInputStream());
        List<User> users = userService.createBulkUser(usersToCreate);

        String response = String.format("Created %d users.", users.size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
