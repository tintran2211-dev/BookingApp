package net.train.Booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.RoleAlreadyExistException;
import net.train.Booking.model.Role;
import net.train.Booking.model.User;
import net.train.Booking.service.IServices.IRoleServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.FOUND;

@RequestMapping("/role/")
@RestController
@RequiredArgsConstructor
public class RoleController {
    private final IRoleServices _roleService;


    @GetMapping("get/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(_roleService.getRoles(),FOUND);
    }

    @PostMapping("post/create-role")
    public ResponseEntity<String> createRole(@RequestBody Role theRole){
        try {
            _roleService.createRoles(theRole);
            return ResponseEntity.ok("New role created successfully!");
        }catch (RoleAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        _roleService.deleteRole(roleId);
    }

    @PostMapping("remove/user-from-role")
    public User removeUsersFromRole(
            @RequestParam("roleId") Long roleId,
            @RequestParam("userId") Long userId){
        return _roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping({"remove/all-user-from-role/{roleId}"})
    public Role removeAllUserFromRole(@PathVariable("roleId") Long roleId){
        return _roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("assign/user-to-role")
    public User assignUserToRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId){
        return _roleService.assignRoletoUser(userId, roleId);
    }
}
