变更记录：
1，支持propertyIgnoreAll所有表忽略属性
2，支持PropertyIgnore对应指定表忽略属性
3，支持nextSeqId,selectSeqId
4，支持批量nextSeqIds,selectSeqIds
5，支持selectMaxCid查最大id值
6，支持selectMaxModifyDateByExample查数据最大修改时间
7，支持insertList如果cid为空则取序列，否则直接保存
8，支持SqlHelper.getAllProperties(Class<?> entityClass, String ignoreProperties)，以取得忽略指定属性后剩下的所有属性

待处理问题：
1，insert保存后不能直接返回cid，替代方案通过nextSeqId预先生成再保存
2，insertList保存后不能直接返回cid
