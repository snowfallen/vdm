package vdm.shop.repository.user;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u LEFT JOIN FETCH u.roles r "
            + "WHERE u.email = :email "
            + "AND r.isDeleted = FALSE")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r "
            + "WHERE r.id <> 2 "
            + "AND r.isDeleted = FALSE")
    Page<User> findAllNotClient(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r "
            + "WHERE r.id = 3 "
            + "AND r.isDeleted = FALSE")
    Page<User> findAllAccountant(Pageable pageable);
}
