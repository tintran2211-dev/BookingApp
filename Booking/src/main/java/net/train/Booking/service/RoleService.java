package net.train.Booking.service;

import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.RoleAlreadyExistException;
import net.train.Booking.exception.UserAlreadyExistsException;
import net.train.Booking.model.Role;
import net.train.Booking.model.User;
import net.train.Booking.repository.RoleRepository;
import net.train.Booking.repository.UserRepository;
import net.train.Booking.service.IServices.IRoleServices;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleService implements IRoleServices {
    private final RoleRepository _roleRepository;
    private final UserRepository _userRepository;
    @Override
    public List<Role> getRoles() {
        return _roleRepository.findAll();
    }

    @Override
    public Role createRoles(Role theRole) {
        String roleName = "ROLE_" + theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        if(_roleRepository.existsByName(roleName)){
            throw new RoleAlreadyExistException("The role "+theRole.getName()+" already exist");
        }
        return _roleRepository.save(role);
    }

    @Override
    public Role findByName(String name) {
        return _roleRepository.findByName(name).get();
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> role = _roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUsersFromRole);
        return _roleRepository.save(role.get());
    }

    @Override
    public void deleteRole(Long id) {
        this.removeAllUsersFromRole(id);
        _roleRepository.deleteById(id);
    }

    //Xóa người dùng khỏi vai trò
    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = _userRepository.findById(userId);
        Optional<Role> role = _roleRepository.findById(roleId);
        if(role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            _roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User assignRoletoUser(Long userId, Long roleId) {
        Optional<User> user = _userRepository.findById(userId);
        Optional<Role> role = _roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(
                    user.get().getFirstName()+" is already assigned to the " +role.get().getName()+" role"
            );
        }
        if(role.isPresent()){
            role.get().assignRoleToUser(user.get());
            _roleRepository.save(role.get());
        }
        return user.get();
    }
}
