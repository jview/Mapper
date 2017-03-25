package tk.mybatis.mapper.common;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * support mysql insertList
 * @since 2015-09-06 21:53
 */
public interface MapperMy<T> extends Mapper<T>, MySqlMapper<T> {

}
