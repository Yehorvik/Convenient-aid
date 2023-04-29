package ua.edu.sumdu.volonteerProject.controllers;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.DTO.UserInfoDTO;
import ua.edu.sumdu.volonteerProject.errors.AuthorityNotFoundException;
import ua.edu.sumdu.volonteerProject.security.CustomUserDetailsService;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
//@PreAuthorize("ADMIN")
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class UserSettingsController {

    CustomUserDetailsService customUserDetailsService;

    @PatchMapping("/blockUser/{username}")
    public ResponseEntity setBlocked(@PathVariable String username, @RequestBody Map<String, Boolean> isBlocked){
        customUserDetailsService.setUserBlocked(username,isBlocked.get("isBlocked"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/addRoleToUser/{username}")
    public ResponseEntity addRoleToUser(@PathVariable String username, @RequestBody Map<String, String> newUserRole) throws AuthorityNotFoundException {
       customUserDetailsService.addUserNewRoleByEmail(username, newUserRole.get("newUserRole"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deleteRoleOfUser/{username}")
    public ResponseEntity deleteRoleOfUser(@PathVariable String username, @RequestBody Map<String, String> deletedRole) throws AuthorityNotFoundException {
        customUserDetailsService.deleteUserRoleByEmail(username, deletedRole.get("deletedRole"));
        return ResponseEntity.ok().build();
    }



    @GetMapping("/getUserCount")
    public long getUserCount(){
        return customUserDetailsService.getCountOfUsers();
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsersForPage(@RequestParam int page){
        List<JwtUserDetails> userDetailsList = customUserDetailsService.getAllUsersByPage(page);
        List<UserInfoDTO> userInfoDTOS = userDetailsList.stream().map(DtoConverterUtils::convertUserInfo).collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(userInfoDTOS);
    }
}
