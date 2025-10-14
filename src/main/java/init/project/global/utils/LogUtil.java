package init.project.global.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public class LogUtil {

    private final StringBuilder buffer = new StringBuilder();
    private final String processName;
    private final String prefix;
    private final StopWatch stopWatch = new StopWatch();

    public LogUtil(String processName, Long userId, Long schdId, String msgType) {
        this.processName = processName;
        this.prefix = String.format("(userId: %d / schdId: %d / msgType: %s) ", userId, schdId, msgType);
        this.stopWatch.start();
    }

    /** 메시지를 로그 버퍼에 추가합니다. */
    public void log(String msg) {
        buffer.append(msg);
    }

    /** [INFO] 로그 출력 */
    public void flushInfoLog() {
        stop();
        logElapsedTime();
        log.info("[{}] {}{}", processName, prefix, buffer);
    }

    /** [WARN] 로그 출력 */
    public void flushWarnLog() {
        stop();
        logElapsedTime();
        log.warn("[{}] {}{}", processName, prefix, buffer);
    }

    /** [ERROR] 로그 출력 */
    public void flushErrorLog(Exception e) {
        stop();
        logElapsedTime();
        log.error("[{}] {}{}", processName, prefix, buffer, e);
    }

    /** 현재까지 누적된 로그를 직접 반환하고 싶을 때 사용 (예: 외부에서 문자열로 보고 싶을 때). */
    public String getBufferedLog() {
        return buffer.toString();
    }

    /** StopWatch 멈추기 */
    private void stop() {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
    }

    /** 경과 시간 메시지를 로그에 추가 */
    private void logElapsedTime() {
        log("\n => elapsedTime: " + stopWatch.getTotalTimeMillis() + "ms");
    }

}