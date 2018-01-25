package com.jd.legal.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author : weipengyu
 * @date : 2018/1/22 16:12
 * Description:
 */
public interface BaseService<EntityDto>{

    /**
     * 创建entityDto
     * @param entityDto
     * @return
     */
    int insert(EntityDto entityDto);

    /**
     * 批量创建entityDto
     * @param entityDtoList
     * @return
     */
    int insertBatch(List<EntityDto> entityDtoList);

    /**
     * 根据Id查找entityDto
     * @param id
     * @return
     */
    EntityDto selectByPrimaryKey(Long id);

    /**
     * 根据no查找entityDto
     * @param caseNo
     * @return
     */
    EntityDto selectByNo(String no);

    /**
     * 选择性查询，即根据entityDto的非空字段查询
     * @param entityDto
     * @return
     */
    List<EntityDto> selectSelective(EntityDto entityDto);

    /**
     * 根据entityDto的非空字段查询符合条件的个数
     * @param entityDto
     * @return
     */
    int selectCount(EntityDto entityDto);

    /**
     * 分页查询，当pageable==null时，查询全部
     * @param entityDto
     * @param pageable
     * @return
     */
    Page<EntityDto> selectPage(EntityDto entityDto, Pageable pageable);

    /**
     * 根据entityDto的id进行更新
     * @param entityDto
     * @return
     */
    int updateByPrimaryKey(EntityDto entityDto);

    /**
     * 根据entityDto的id进行删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据entityDto的编号进行删除
     * @param no
     * @return
     */
    int deleteByNo(String no);

}
