package init.project.domain.users.repository;

import init.project.domain.users.model.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface UserAccountRepository {

    Optional<UserAccount> findById(@Param("userId") Long userId);
    Optional<UserAccount> findByEmail(@Param("email") String email);

    Integer save(@Param("userAccount") UserAccount userAccount);

}