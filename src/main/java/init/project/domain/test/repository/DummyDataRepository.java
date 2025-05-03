package init.project.domain.test.repository;

import init.project.domain.test.model.DummyData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DummyDataRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String findById = """
        SELECT ID,
               DATA1,
               DATA2,
               DATA3,
               DEL_YN,
               REG_DT,
               UPT_DT
        FROM TB_DUMMY_DATA
        WHERE ID = :id
          AND DEL_YN = 'N'
    """;

    private final String findAll = """
        SELECT ID,
               DATA1,
               DATA2,
               DATA3,
               DEL_YN,
               REG_DT,
               UPT_DT
        FROM TB_DUMMY_DATA
        WHERE DEL_YN = 'N'
    """;

    private final String saveDummyData = """
        INSERT INTO TB_DUMMY_DATA
            ( DATA1, DATA2, DATA3 )
        VALUES
            ( :data1, :data2, :data3 )
    """;

    private final String updateDummyData = """
        UPDATE TB_DUMMY_DATA
        SET DATA1 = :data1
           ,DATA2 = :data2
           ,DATA3 = :data3
        WHERE ID = :id
    """;

    private final String deleteDummyData = """
        UPDATE TB_DUMMY_DATA
        SET DEL_YN = 'Y'
        WHERE ID = :id
    """;

    public List<DummyData> findAll() {
        SqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(findAll, params, (rs, rowNum) ->
                DummyData.builder()
                        .id(rs.getLong("ID"))
                        .data1(rs.getString("DATA1"))
                        .data2(rs.getString("DATA2"))
                        .data3(rs.getString("DATA3"))
                        .delYn(rs.getString("DEL_YN"))
                        .regDt(rs.getTimestamp("REG_DT").toLocalDateTime())
                        .uptDt(rs.getTimestamp("UPT_DT") != null ? rs.getTimestamp("UPT_DT").toLocalDateTime() : null)
                        .build()
        );
    }

    public Optional<DummyData> findById(Long id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(findById, params, (rs, rowNum) ->
                    DummyData.builder()
                            .id(rs.getLong("ID"))
                            .data1(rs.getString("DATA1"))
                            .data2(rs.getString("DATA2"))
                            .data3(rs.getString("DATA3"))
                            .delYn(rs.getString("DEL_YN"))
                            .regDt(rs.getTimestamp("REG_DT").toLocalDateTime())
                            .uptDt(rs.getTimestamp("UPT_DT") != null ? rs.getTimestamp("UPT_DT").toLocalDateTime() : null)
                            .build()
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int saveDummyData(DummyData dummyData) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("data1", dummyData.getData1())
                .addValue("data2", dummyData.getData2())
                .addValue("data3", dummyData.getData3());

        return jdbcTemplate.update(saveDummyData, params);
    }

    public void saveDummyDataList(List<DummyData> dummyDataList, int batchInsertSize) {
        int totalAttemptCount = dummyDataList.size();
        int totalSuccessCount = 0;
        int totalFailureCount = 0;

        for (int i = 0; i < totalAttemptCount; i += batchInsertSize) {
            int end = Math.min(i + batchInsertSize, totalAttemptCount);
            List<DummyData> subList = dummyDataList.subList(i, end);

            SqlParameterSource[] params = subList.stream()
                    .map(info -> new MapSqlParameterSource()
                            .addValue("data1", info.getData1())
                            .addValue("data2", info.getData2())
                            .addValue("data3", info.getData3())
                    ).toArray(SqlParameterSource[]::new);

            int[] resultList = jdbcTemplate.batchUpdate(saveDummyData, params);
            for (int result : resultList) {
                if (result == 1) {
                    totalSuccessCount++;
                } else {
                    totalFailureCount++;
                }
            }
        }
        if (totalAttemptCount != totalSuccessCount) {
            throw new IllegalStateException(
                    String.format("Batch Insert 실패: 시도한 건수=%d, 성공한 건수=%d", totalAttemptCount, totalSuccessCount)
            );
        }
    }

    public int updateDummyData(DummyData dummyData) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", dummyData.getId())
                .addValue("data1", dummyData.getData1())
                .addValue("data2", dummyData.getData2())
                .addValue("data3", dummyData.getData3());

        return jdbcTemplate.update(updateDummyData, params);
    }

    public int deleteDummyData(int id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.update(deleteDummyData, params);
    }

}