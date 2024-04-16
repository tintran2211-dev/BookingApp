package net.train.Booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.UserAlreadyExistsException;
import net.train.Booking.model.Role;
import net.train.Booking.model.User;
import net.train.Booking.repository.RoleRepository;
import net.train.Booking.repository.UserRepository;
import net.train.Booking.service.IServices.IUserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository _userRepository;
    private final PasswordEncoder _passwordEncoder;
    private final RoleRepository _roleRepository;

    //Hàm xử lý đăng ký User
    //Logic Username sẽ là email của người dùng
    @Override
    public User registerUser(User user) {
        //Kiểm tài username có tồn tại, nếu đã có thì trả ra exception là đã tồn tại
        if(_userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail()+"Already exists");
        }
        //Sau khi kiểm tra username thì đến phần setpassword
        //Sử dụng PasswordEncoder để mã hoá password
        user.setPassword(_passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        //Sau đó là set role cho user
        Role userRole = _roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));
        return _userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return _userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null){
            _userRepository.deleteByEmail(email);
        }
    }

    @Override
    public User getUser(String email) {
        return _userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
