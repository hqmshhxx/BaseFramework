package com.jd.legal.base;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author : weipengyu
 * @date : 2018/1/22 17:28
 * Description:
 */
public abstract class AbstractBaseService<Repository extends BaseRepository,EntityDto extends Serializable,Entity> implements
        BaseService<EntityDto> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);

    @Autowired
    protected Repository repository;
    @Autowired
    protected ModelMapper modelMapper;

    private Class<EntityDto> entityDtoClass;
    private Class<Entity> entityClass;
  

    @PostConstruct
    public void init() {
        //this指代的是子类的对象，this.getClass()返回代表子类的Class对象，
        //再接着调用getGenericSuperclass()方法，可以返回代表子类的直接
        //超类（也就是AbstractBaseDao类）的Type对象。因为它是泛型，因此可强制类型
        //转换为ParameterizedType类型，再调用getActualTypeArguments()
        //方法，可获得子类给泛型指定的实际类型的数组。因为这里只有一个泛型
        //参数，所以取数组的第1个元素，即为entity的类型。entityClass代表entity的类型。
        @SuppressWarnings("unchecked")
//        Class<T> entityType = (Class<T>) ((ParameterizedType) this.getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[1];
        Type[] typeArray = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        this.entityDtoClass = (Class<EntityDto>)typeArray[1];
        this.entityClass = (Class<Entity>)typeArray[2];
    }

    /**
     * 创建entityDto
     * @param entityDto
     * @return
     */
    @Override
    public int insert(EntityDto entityDto) {
        int result = 0;
        try {
            result = repository.insert(modelMapper.map(entityDto, entityClass));
        } catch (Exception e) {
            logger.error("BaseService insert Exception",e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 批量创建entityDto
     *
     * @param entityDtoList
     * @return
     */
    @Override
    public int insertBatch(List<EntityDto> entityDtoList) {
        int result = 0;
        List<Entity> entityList = new ArrayList<>();
        try {
            if(entityDtoList != null){
                for(EntityDto entityDto : entityDtoList){
                    entityList.add(modelMapper.map(entityDto,entityClass));
                }
            }
            result = repository.insertBatch(entityList);
        } catch (Exception e) {
            logger.error("BaseService insertBatch Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * 根据Id查找entityDto
     *
     * @param id
     * @return
     */
    @Override
    public EntityDto selectByPrimaryKey(Long id) {
        EntityDto entityDto = null;
        try{
            Entity entity = (Entity)repository.selectByPrimaryKey(id);
            if(entity != null){
                entityDto = modelMapper.map(entity,entityDtoClass);
            }
        }catch (Exception e){
            logger.error("BaseService selectByPrimaryKey Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return entityDto;
    }

    /**
     * 根据no查找entityDto
     *
     * @param caseNo
     * @return
     */
    @Override
    public EntityDto selectByNo(String no) {
        EntityDto entityDto = null;
        try{
            Entity entity = (Entity)repository.selectByNo(no);
            if(entity != null){
                entityDto = modelMapper.map(entity,entityDtoClass);
            }
        }catch (Exception e){
            logger.error("BaseService selectByNo Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return entityDto;
    }

    /**
     * 选择性查询，即根据entityDto的非空字段查询
     *
     * @param entityDto
     * @return
     */
    @Override
    public List<EntityDto> selectSelective(EntityDto entityDto) {
        List<EntityDto> entityDtoList = null;
        List<Entity> entityList = null;
        try {
            if (entityDto != null) {
                entityList = repository.selectSelective(modelMapper.map(entityDto, entityClass));
            }
            if (entityList != null) {
                entityDtoList = new ArrayList<>();
                for(Entity entity : entityList){
                    entityDtoList.add(modelMapper.map(entity,entityDtoClass));
                }
            }
        } catch (Exception e) {
            logger.error("BaseService selectSelective Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return entityDtoList;
    }

    /**
     * 根据entityDto的非空字段查询符合条件的个数
     *
     * @param entityDto
     * @return
     */
    @Override
    public int selectCount(EntityDto entityDto) {
        int count = 0;
        try{
            if(entityDto != null){
                count = repository.selectCount((modelMapper.map(entityDto,entityClass)));
            }
        }catch(Exception e){
            logger.error("BaseService selectCount Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return count;
    }

    /**
     * 分页查询，当pageable==null时，查询全部
     *
     * @param entityDto
     * @param pageable
     * @return
     */
    @Override
    public Page selectPage(EntityDto entityDto, Pageable pageable) {
        List<Entity> entityList = null;
        List<EntityDto> entityDtoList = null;
        Page<EntityDto> page = null;
        int count = 0;
        try{
            if(entityDto != null){
                Entity entity = modelMapper.map(entityDto,entityClass);
                count = repository.selectCount(entity);
                if(count > 0){
                    if(pageable == null){
                        entityList = repository.selectSelective(entity);
                    }else{
                        entityList = repository.selectPage(entity,pageable);
                    }
                    if(entityList != null){
                        entityDtoList = new ArrayList<>();
                        for(Entity en : entityList){
                            entityDtoList.add(modelMapper.map(en,entityDtoClass));
                        }
                        page = new PageImpl<EntityDto>(entityDtoList, pageable, count);
                    }else{
                        page = new PageImpl<EntityDto>(Collections.<EntityDto>emptyList(), pageable, 0);
                    }
                }else{
                    page = new PageImpl<EntityDto>(Collections.<EntityDto>emptyList(), pageable, 0);
                }
            }else{
                page = new PageImpl<EntityDto>(Collections.<EntityDto>emptyList(), pageable, 0);
            }
        }catch (Exception e){
            logger.error("BaseService selectPage Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return page;
    }

    /**
     * 根据entityDto的id进行更新
     *
     * @param entityDto
     * @return
     */
    @Override
    public int updateByPrimaryKey(EntityDto entityDto) {
        int result = 0;
        try {
            if (entityDto != null) {
                result = repository.updateByPrimaryKey(modelMapper.map(entityDto, entityClass));
            }
        } catch (Exception e) {
            logger.error("BaseService updateByPrimaryKey Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * 根据entityDto的id进行删除
     *
     * @param id
     * @return
     */
    @Override
    public int deleteByPrimaryKey(Long id) {
        int result =0;
        try{
            result =  repository.deleteByPrimaryKey(id);
        }catch (Exception e){
            logger.error("BaseService deleteByPrimaryKey Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * 根据entityDto的编号进行删除
     *
     * @param no
     * @return
     */
    @Override
    public int deleteByNo(String no) {
        int result =0;
        try{
            result =  repository.deleteByNo(no);
        }catch (Exception e){
            logger.error("BaseService deleteByNo Exception",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
