package com.znzn.tutorial.repository;

import com.znzn.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authroties") // 쿼리가 수행 될때 Lazy 조회가 아닌 Eager 조회로 authorities 정보를 같이 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
