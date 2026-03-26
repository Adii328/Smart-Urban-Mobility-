package com.smartmobility.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Custom query to sum all reward balances
    @Query("SELECT SUM(u.rewardBalance) FROM User u")
    Double sumAllRewardBalances();
}

