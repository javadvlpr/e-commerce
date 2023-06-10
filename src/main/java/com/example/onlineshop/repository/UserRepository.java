ackage com.example.onlineshop.repository;

import com.example.onlineshop.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    @Query(value = "select * from users where id =?1 and is_deleted = false ",nativeQuery = true)
    Optional<UserEntity> findById(Long id);
    @Query(value = "select * from users where username =?1 and is_deleted = false ",nativeQuery = true)
    Optional<UserEntity> findByUsername(String username);
    @Query(value = "select * from users u where u.chat_id=?1 and u.is_deleted = false",nativeQuery = true)
    Optional<UserEntity> findByChatId(Long id);
}
