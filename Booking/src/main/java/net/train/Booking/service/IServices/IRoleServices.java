package net.train.Booking.service.IServices;

import net.train.Booking.model.Role;
import net.train.Booking.model.User;

import java.util.List;

public interface IRoleServices {
    List<Role> getRoles();
    Role createRoles(Role theRole);
    Role findByName(String name);
    Role removeAllUsersFromRole(Long roleId);
    void deleteRole(Long id);
    User removeUserFromRole(Long userId, Long roleId);
    User assignRoletoUser(Long userId, Long roleId);
}
