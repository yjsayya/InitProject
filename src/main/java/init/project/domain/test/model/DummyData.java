package init.project.domain.test.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DummyData {

    private Long id;
    private String data1;
    private String data2;
    private String data3;

    private String delYn;
    private LocalDateTime regDt;
    private LocalDateTime uptDt;

}