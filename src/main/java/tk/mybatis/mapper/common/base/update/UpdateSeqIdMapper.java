package tk.mybatis.mapper.common.base.update;

import org.apache.ibatis.annotations.UpdateProvider;

import tk.mybatis.mapper.provider.base.BaseUpdateProvider;

public interface UpdateSeqIdMapper<T> {

	  @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
	  Long selectSeqId(T record);
	    
	  @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
	  Long updateSeqId(T record);
	  
	  @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
	  Long nextSeqId(T record);
}
