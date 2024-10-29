package com.hxt.matchMate.bean.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.hxt.matchMate.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * ClassName: TeamQueryRequest
 * Package: com.hxt.matchMate.bean.request
 * Description:
 *          查询队伍信息封装
 * @Author hxt
 * @Create 2024/9/19 15:14
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {

    private static final long serialVersionUID = 5L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private List<Long> idList;
    private String name;

    private String searchText;
    private String description;

    private Integer maxNum;

    private Long userId;

    private Integer status;

}
