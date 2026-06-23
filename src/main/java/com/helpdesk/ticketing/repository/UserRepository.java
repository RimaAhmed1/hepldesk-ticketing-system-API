package com.helpdesk.ticketing.repository;
import com.helpdesk.ticketing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
}
