package com.aenggukland.letspt.board;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardSearchRequest {
    private BoardCategory category;
    private int pageSize = 10;
    private int offset;
    private int pageNum;
    private BoardSearchCategory searchCategory;
    private String searchKeyword;

    public BoardSearchRequest(BoardCategory category, int pageNum, BoardSearchCategory boardSearchCategory, String searchKeyword) {
        if(pageNum > 0){
            this.offset = pageNum * 10 - 10;
        } else {
            throw new BusinessException(ErrorCode.INVALID_BOARD_PAGE_NUM);
        }
        this.category = category;
        this.pageNum = pageNum;
        this.searchCategory = boardSearchCategory;
        this.searchKeyword = searchKeyword;
    }
}
