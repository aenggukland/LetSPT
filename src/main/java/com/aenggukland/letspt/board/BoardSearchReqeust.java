package com.aenggukland.letspt.board;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardSearchReqeust {
    private BoardCategory category;
    private int pageSize = 10;
    private int offset;
    private int pageNum;
    private BoardSearchCategory searchCategory;
    private String searchKeyword;

    public void setOffset(int pageNum) {
        if(pageNum > 0){
            this.offset = pageNum * 10 - 10;
        } else {
            throw new BusinessException(ErrorCode.INVALID_BOARD_PAGE_NUM);
        }
    }
}
