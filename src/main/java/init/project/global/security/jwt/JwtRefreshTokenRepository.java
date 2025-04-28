package init.project.global.security.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface JwtRefreshTokenRepository {

    Optional<JwtRefreshToken> findByUserId(@Param("userId") Long userId);

    void saveRefreshToken(@Param("jwtRefreshToken") JwtRefreshToken jwtRefreshToken);
    void updateRefreshToken(@Param("jwtRefreshToken") JwtRefreshToken jwtRefreshToken);

}