package com.jd.legal.base;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author : weipengyu
 * @date : 2018/1/22 16:12
 * Description:
 */
public interface BaseRepository<Entity> {

    /**
     * 将entity插入数据库
     * @param entity
     * @return 插入成功的个数
     */
    int insert(Entity entity);

    /**
     * 将entityList批量插入数据库
     * @param entityList
     * @return 插入成功的个数
     */
    int insertBatch(List<Entity> entityList);

    /**
     * 根据Id查找entity
     * @param id
     * @return entity
     */
    Entity selectByPrimaryKey(Long id);

    /**
     * 根据no查找entity
     * @param no
     * @return entity
     */
    Entity selectByNo(String no);

    /**
     * 选择性查询，即根据entity的非空字段查询
     * @param entity
     * @return
     */
    List<Entity> selectSelective(Entity entity);

    /**
     * 根据entity的非空字段查询符合条件的个数
     * @param entity
     * @return
     */
    int selectCount(Entity entity);

    /**
     * 根据entity的id进行更新
     * @param entity
     * @return
     */
    int updateByPrimaryKey(Entity entity);

    /**
     * 根据entity的id进行删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据entity的编号进行删除
     * @param entity
     * @return
     */
    int deleteByNo(String no);

    /**
     * 根据 entity 条件，查询全部记录并翻页,当pageable == null时，不分页。
     * @param entity
     * @param pageable
     * @return
     */
    List<Entity> selectPage(@Param("entity") Entity entity, @Param("pageable") Pageable pageable);

}
