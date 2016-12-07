/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tk.mybatis.mapper.provider;

import java.sql.SQLException;
import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

/**
 * SpecialProvider实现类，特殊方法实现类
 *
 * @author liuzh
 */
public class SpecialProvider extends MapperTemplate {

    public SpecialProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    private static String driverName=null;
    private boolean isMysql(MappedStatement ms){
    	boolean isMysql=false;
    	if(driverName==null){
    		try {
				driverName=ms.getConfiguration().getEnvironment().getDataSource().getConnection().getMetaData().getDriverName();;
			} catch (SQLException e) {
//				e.printStackTrace();
				driverName="no";
			}
    	}
//    	System.out.println("-----driverName="+driverName);
    	isMysql=driverName.toLowerCase().indexOf("mysql")>=0;
    	return isMysql;
    }
    /**
     * 批量插入
     *
     * @param ms
     */
    public String insertList(MappedStatement ms) {
       if(this.isMysql(ms)){
    	   return this.insertListMysql(ms);
       }
       //用于支持非mysql的数据库
       else{
			final Class<?> entityClass = getEntityClass(ms);
			// 开始拼sql
			StringBuilder sql = new StringBuilder();
			sql.append(
					"<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\" >");
			sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
			sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
			// 获取全部列
			Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
			sql.append(" VALUES ");
			sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

			// 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
			String sqlCValue = "";
			String seqName = null;
			for (EntityColumn column : columnList) {
				// 获取id seqName
				if (column.isId()) {
					seqName = column.getSequenceName();
					if (seqName != null && seqName.startsWith("select")) {
						seqName = seqName.substring("select".length());
					}
					sqlCValue += seqName + ",";
				}
				if (!column.isId() && column.isInsertable()) {
					sqlCValue += column.getColumnHolder("item") + ",";
				}
			}
			sql.append(sqlCValue);
			sql.append("</trim>");
			sql.append("</foreach>");
//			System.out.println("-----sql=" + sql.toString());
			return sql.toString();
       }
    }
    
    /**
     * 批量插入
     *
     * @param ms
     */
    public String insertListById(MappedStatement ms) {
       if(this.isMysql(ms)){
    	   return this.insertListMysqlById(ms);
       }
       //用于支持非mysql的数据库
       else{
			final Class<?> entityClass = getEntityClass(ms);
			// 开始拼sql
			StringBuilder sql = new StringBuilder();
			sql.append(
					"<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\" >");
			sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
			sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
			// 获取全部列
			Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
			sql.append(" VALUES ");
			sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

			// 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
			String sqlCValue = "";
			String seqName = null;
			for (EntityColumn column : columnList) {
				// 获取id seqName
				if (column.isInsertable()) {
					sqlCValue += column.getColumnHolder("item") + ",";
				}
			}
			sql.append(sqlCValue);
			sql.append("</trim>");
			sql.append("</foreach>");
//			System.out.println("-----sql=" + sql.toString());
			return sql.toString();
       }
    }
    
    private String insertListMysql(MappedStatement ms) {
		final Class<?> entityClass = getEntityClass(ms);
		// 开始拼sql
		StringBuilder sql = new StringBuilder();
		sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
		sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
		sql.append(" VALUES ");
		sql.append("<foreach collection=\"list\" item=\"item\" separator=\",\" >");
		sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
		// 获取全部列
		Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);

		// 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
		String seqName = null;
		for (EntityColumn column : columnList) {
			// 获取id seqName
			if (column.isId()) {
				seqName = column.getSequenceName();
				if (seqName != null && seqName.startsWith("select")) {
					seqName = seqName.substring("select".length());
				}
				sql.append(seqName + ",");
			}
			if (!column.isId() && column.isInsertable()) {
				sql.append(column.getColumnHolder("item") + ",");
			}
		}
		sql.append("</trim>");
		sql.append("</foreach>");
		if(" _nextval('_yc_orderPayFlow_cid_seq')".equals(seqName)){
			System.out.println("-----sql="+sql.toString());
		}
		return sql.toString();
    	
    }
    
    private String insertListMysqlById(MappedStatement ms) {
		final Class<?> entityClass = getEntityClass(ms);
		// 开始拼sql
		StringBuilder sql = new StringBuilder();
		sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
		sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
		sql.append(" VALUES ");
		sql.append("<foreach collection=\"list\" item=\"item\" separator=\",\" >");
		sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
		// 获取全部列
		Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);

		// 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
		String seqName = null;
		for (EntityColumn column : columnList) {
			
			if (column.isInsertable()) {
				sql.append(column.getColumnHolder("item") + ",");
			}
		}
		sql.append("</trim>");
		sql.append("</foreach>");
		if(" _nextval('_yc_orderPayFlow_cid_seq')".equals(seqName)){
			System.out.println("-----sql="+sql.toString());
		}
		return sql.toString();
    	
    }
    
    /**
     * 批量修改
     *
     * @param ms
     */
    public String updateListByIdSelective(MappedStatement ms) {
        
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if(this.isMysql(ms)){
        	sql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\" >");
        }
        else{
        	sql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"begin\" close=\"end;\" separator=\";\" >");
        }
        
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, "item", true, isNotEmpty()));
        sql.append(this.wherePKColumns(entityClass, "item"));
        sql.append("</foreach>");
//        System.out.println("----update="+sql);
        return sql.toString();
    }
    
    /**
     * 批量修改
     *
     * @param ms
     */
    public String updateListById(MappedStatement ms) {
        
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if(this.isMysql(ms)){
        	sql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\" >");
        }
        else{
        	sql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"begin\" close=\"end;\" separator=\";\" >");
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, "item", false, !isNotEmpty()));
        sql.append(this.wherePKColumns(entityClass, "item"));
        sql.append("</foreach>");
        return sql.toString();
    }

    private String wherePKColumns(Class<?> entityClass, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(" and " + column.getColumnEqualsHolder(entityName));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 插入，主键id，自增
     *
     * @param ms
     */
    public String insertUseGeneratedKeys(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(SqlHelper.insertValuesColumns(entityClass, true, false, false));
        return sql.toString();
    }
}
