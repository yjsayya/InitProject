package init.project.global.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PagingUtil {

    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final Set<Integer> VALID_PAGE_SIZES = Set.of(5, 10, 15, 30, 50);
    private static final int PAGE_BLOCK_COUNT = 5;

    public static PagingInfo getPagingInfo(int currentPage, int pageSize, String orderDirection, long totalRowDataCnt) {
        /** STEP 1 */
        pageSize = checkPageSizeValidation(pageSize);
        int totalPageBlockCnt = calTotalPageBlockCnt(totalRowDataCnt, pageSize);
        currentPage = checkCurrentPageValidation(currentPage, totalPageBlockCnt);
        /** STEP 2 */
        int endPageBlock = calEndPageBlock(currentPage, PAGE_BLOCK_COUNT);
        int startPageBlock = calStartPageBlock(endPageBlock, PAGE_BLOCK_COUNT);
        // 유효성 검사를 더 늦게 해줘야 함
        endPageBlock = checkEndPageBlockValidation(endPageBlock, totalPageBlockCnt);
        boolean next = (currentPage != totalPageBlockCnt);

        startPageBlock = checkStartPageBlockValidation(startPageBlock);
        boolean prev = (currentPage != 1);
        /** STEP 3 */
        int startRowDataNum = (currentPage-1) * pageSize + 1;
        int endRowDataNum = startRowDataNum + pageSize - 1;

        int offSet = (currentPage-1) * pageSize;

        return PagingInfo.builder()
                .pageSize(pageSize)
                .offSet(offSet)
                .currentPage(currentPage)
                .startPageBlock(startPageBlock)
                .endPageBlock(endPageBlock)
                .startRowDataNum(startRowDataNum)
                .endRowDataNum(endRowDataNum)
                .totalPageBlockCnt(totalPageBlockCnt)
                .totalRowDataCnt(totalRowDataCnt)
                .prev(prev)
                .next(next)
                .orderDirection(orderDirection)
                .build();
    }

    /** pageSize 유효성 검사 */
    private static int checkPageSizeValidation(int pageSize) {
        if (VALID_PAGE_SIZES.contains(pageSize)) {
            return pageSize;
        }
        return DEFAULT_PAGE_SIZE;
    }

    /** totalPageBlockCnt 계산하기 */
    private static int calTotalPageBlockCnt(long totalRowDataCnt, int pageSize) {
        if (totalRowDataCnt == 0) {
            return 1;
        }
        return (int)(totalRowDataCnt + pageSize - 1) / pageSize;
    }

    /** currentPage 유효성 검사 */
    private static int checkCurrentPageValidation(int currentPage, int totalPageBlockCnt) {
        if (currentPage > totalPageBlockCnt)
            currentPage = totalPageBlockCnt;
        else if (currentPage < 1)
            currentPage = 1;

        return currentPage;
    }

    /** endPageBlock 계산하기 */
    private static int calEndPageBlock(int currentPage, int pageBlockCnt) {
        return ((currentPage + pageBlockCnt - 1) / pageBlockCnt) * pageBlockCnt;
    }

    /** endPageBlock 유효성 검사 */
    private static int checkEndPageBlockValidation(int endPageBlock, int totalPageBlockCnt) {
        return Math.min(endPageBlock, totalPageBlockCnt);
    }

    /** startPageBlock 계산하기 */
    private static int calStartPageBlock(int endPageBlock, int pageBlockCnt) {
        return endPageBlock - pageBlockCnt + 1;
    }

    /** startPageBlock 유효성 검사 */
    private static int checkStartPageBlockValidation(int startPageBlock) {
        return Math.max(startPageBlock, 1);
    }

    @Getter
    @Builder
    @ToString
    public static class PagingInfo {

        private int pageSize;
        private int offSet;

        private String orderDirection;
        private int currentPage;
        private int startPageBlock;
        private int endPageBlock;
        private int startRowDataNum;
        private int endRowDataNum;
        private int totalPageBlockCnt;
        private long totalRowDataCnt;
        private boolean prev;
        private boolean next;

    }

    @Setter
    @ToString
    public static class PagingRQ {

        private Integer currentPage;
        private Integer pageSize;
        private String orderDirection;

        public Integer getCurrentPage() { return Objects.requireNonNullElse(this.currentPage, 1); }
        public Integer getPageSize() { return Objects.requireNonNullElse(this.pageSize, 10); }
        public String getOrderDirection() { return "asc".equalsIgnoreCase(this.orderDirection) ? "ASC" : "DESC"; }

    }

    @Getter
    @Builder
    public static class PagingRS<T> {
        private List<T> data;
        private PagingInfo pageInfo;
    }

}