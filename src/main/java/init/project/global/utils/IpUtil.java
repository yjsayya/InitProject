package init.project.global.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class IpUtil {

    // 헤더 순서는 우선순위가 높은 순서대로 나열하는 것이 좋습니다.
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /** 현재 요청의 클라이언트 IP 주소를 반환합니다. */
    public static String getUserIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "0.0.0.0";
        }
        HttpServletRequest request = attributes.getRequest();
        return getClientIp(request);
    }

    /** HttpServletRequest에서 클라이언트 IP를 추출합니다. */
    public static String getClientIp(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                // X-Forwarded-For의 경우 여러 IP가 콤마로 구분되어 올 수 있음 (첫 번째가 클라이언트 IP)
                return ipList.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

}