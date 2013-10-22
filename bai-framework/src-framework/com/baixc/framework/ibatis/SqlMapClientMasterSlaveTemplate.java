/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baixc.framework.ibatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.orm.ibatis.SqlMapClientOperations;
import org.springframework.util.Assert;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * Helper class that simplifies data access via the iBATIS
 * {@link com.ibatis.sqlmap.client.SqlMapClient} API, converting checked
 * SQLExceptions into unchecked DataAccessExceptions, following the
 * <code>org.springframework.dao</code> exception hierarchy.
 * Uses the same {@link org.springframework.jdbc.support.SQLExceptionTranslator}
 * mechanism as {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * <p>The main method of this class executes a callback that implements a
 * data access action. Furthermore, this class provides numerous convenience
 * methods that mirror {@link com.ibatis.sqlmap.client.SqlMapExecutor}'s
 * execution methods.
 *
 * <p>It is generally recommended to use the convenience methods on this template
 * for plain query/insert/update/delete operations. However, for more complex
 * operations like batch updates, a custom SqlMapClientCallback must be implemented,
 * usually as anonymous inner class. For example:
 *
 * <pre class="code">
 * getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
 * 	 public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
 * 		 executor.startBatch();
 * 		 executor.update("insertSomething", "myParamValue");
 * 		 executor.update("insertSomethingElse", "myOtherParamValue");
 * 		 executor.executeBatch();
 * 		 return null;
 * 	 }
 * });</pre>
 *
 * The template needs a SqlMapClient to work on, passed in via the "sqlMapClient"
 * property. A Spring context typically uses a {@link SqlMapClientFactoryBean}
 * to build the SqlMapClient. The template an additionally be configured with a
 * DataSource for fetching Connections, although this is not necessary if a
 * DataSource is specified for the SqlMapClient itself (typically through
 * SqlMapClientFactoryBean's "dataSource" property).
 *
 * @author Juergen Hoeller
 * @since 24.02.2004
 * @see #execute
 * @see #setSqlMapClient
 * @see #setDataSource
 * @see #setExceptionTranslator
 * @see SqlMapClientFactoryBean#setDataSource
 * @see com.ibatis.sqlmap.client.SqlMapClient#getDataSource
 * @see com.ibatis.sqlmap.client.SqlMapExecutor
 */
public class SqlMapClientMasterSlaveTemplate extends JdbcAccessor implements SqlMapClientOperations, InitializingBean {

	private SqlMapClient sqlMapClientMaster;
	private SqlMapClient[] sqlMapClientSlaves;
	
	private Resource configLocation;
	private DataSource master;
	private DataSource[] slaves;

	/**
	 * Create a new SqlMapClientTemplate.
	 */
	public SqlMapClientMasterSlaveTemplate() {
	}

	/**
	 * Create a new SqlMapTemplate.
	 * @param sqlMapClient iBATIS SqlMapClient that defines the mapped statements
	 */
	public SqlMapClientMasterSlaveTemplate(Resource configLocation, DataSource master, DataSource[] slaves) {
		this.configLocation = configLocation;
		this.master = master;
		this.slaves = slaves;
		afterPropertiesSet();
	}

	/**
	 * 获取主库 SqlMapClient
	 * @return
	 */
	public SqlMapClient getSqlMapClientMaster() {
		return sqlMapClientMaster;
	}

	/**
	 * 获取随机从库 SqlMapClient
	 * @return
	 */
	public SqlMapClient getSqlMapClientSlave() {
		return this.sqlMapClientSlaves[Math.abs(new Random().nextInt()) % sqlMapClientSlaves.length];
	}

	/**
	 * 获取所有从库 SqlMapClient 列表
	 * @return
	 */
	public SqlMapClient[] getSqlMapClientSlaves() {
		return sqlMapClientSlaves;
	}

	/**
	 * iBATIS SqlMapClient 的配置文件，如 "WEB-INF/sql-map-config.xml".
	 * @see #setConfigLocations
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * 设置主库数据源
	 * @return
	 */
	public void setMaster(DataSource slaves) {
		this.master = slaves;
	}

	/**
	 * 设置一个从库数据源
	 * @return
	 */
	public void setSlave(DataSource slave) {
		this.slaves = (slave != null ? new DataSource[] {slave} : null);
	}

	/**
	 * 设置所有从库数据源
	 * @return
	 */
	public void setSlaves(DataSource[] slaves) {
		this.slaves = slaves;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.configLocation == null) {
			throw new IllegalArgumentException("Property 'configLocation' is required");
		}
		if (this.master == null) {
			throw new IllegalArgumentException("Property 'master' is required");
		}
		if (this.slaves == null || this.slaves.length<=0) {
			throw new IllegalArgumentException("Property 'slaves' is required");
		}

		try {
			//主库
			SqlMapClientFactoryBean sqlMapClientMasterFactoryBean = new SqlMapClientFactoryBean();
			sqlMapClientMasterFactoryBean.setConfigLocation(configLocation);
			sqlMapClientMasterFactoryBean.setDataSource(master);
			sqlMapClientMasterFactoryBean.afterPropertiesSet();
			this.sqlMapClientMaster = sqlMapClientMasterFactoryBean.getObject();
			
			//从库
			this.sqlMapClientSlaves = new SqlMapClient[slaves.length];
			for(int i=0;i<slaves.length;i++){
				SqlMapClientFactoryBean sqlMapClientSlaveFactoryBean = new SqlMapClientFactoryBean();
				sqlMapClientSlaveFactoryBean.setConfigLocation(configLocation);
				sqlMapClientSlaveFactoryBean.setDataSource(slaves[i]);
				sqlMapClientSlaveFactoryBean.afterPropertiesSet();
				this.sqlMapClientSlaves[i] = sqlMapClientSlaveFactoryBean.getObject();
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Initialize SqlMapClientMasterSlaveTemplate exception", e);
		}
		
		super.afterPropertiesSet();
	}

	public Object queryForObject(String statementName) throws DataAccessException {
		return queryForObject(statementName, null);
	}

	public Object queryForObject(final String statementName, final Object parameterObject)
			throws DataAccessException {

		return execute(new SqlMapClientCallback<Object>() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForObject(statementName, parameterObject);
			}
		}, this.getSqlMapClientSlave());
	}

	public Object queryForObject(
			final String statementName, final Object parameterObject, final Object resultObject)
			throws DataAccessException {
		
		return execute(new SqlMapClientCallback<Object>() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForObject(statementName, parameterObject, resultObject);
			}
		}, this.getSqlMapClientSlave());
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(String statementName) throws DataAccessException {
		return queryForList(statementName, null);
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(final String statementName, final Object parameterObject)
			throws DataAccessException {
		
		return execute(new SqlMapClientCallback<List>() {
			public List doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForList(statementName, parameterObject);
			}
		}, this.getSqlMapClientSlave());
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(String statementName, int skipResults, int maxResults)
			throws DataAccessException {
		return queryForList(statementName, null, skipResults, maxResults);
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(
			final String statementName, final Object parameterObject, final int skipResults, final int maxResults)
			throws DataAccessException {
		
		return execute(new SqlMapClientCallback<List>() {
			public List doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForList(statementName, parameterObject, skipResults, maxResults);
			}
		}, this.getSqlMapClientSlave());
	}

	public void queryWithRowHandler(String statementName, RowHandler rowHandler)
			throws DataAccessException {
		queryWithRowHandler(statementName, null, rowHandler);
	}

	public void queryWithRowHandler(
			final String statementName, final Object parameterObject, final RowHandler rowHandler)
			throws DataAccessException {

		execute(new SqlMapClientCallback<Object>() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.queryWithRowHandler(statementName, parameterObject, rowHandler);
				return null;
			}
		}, this.getSqlMapClientSlave());
	}

	@SuppressWarnings("rawtypes")
	public Map queryForMap(
			final String statementName, final Object parameterObject, final String keyProperty)
			throws DataAccessException {
		
		return execute(new SqlMapClientCallback<Map>() {
			public Map doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForMap(statementName, parameterObject, keyProperty);
			}
		}, this.getSqlMapClientSlave());
	}

	@SuppressWarnings("rawtypes")
	public Map queryForMap(
			final String statementName, final Object parameterObject, final String keyProperty, final String valueProperty)
			throws DataAccessException {
		
		return execute(new SqlMapClientCallback<Map>() {
			public Map doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.queryForMap(statementName, parameterObject, keyProperty, valueProperty);
			}
		}, this.getSqlMapClientSlave());
	}

	public Object insert(String statementName) throws DataAccessException {
		return insert(statementName, null);
	}

	public Object insert(final String statementName, final Object parameterObject)
			throws DataAccessException {

		return execute(new SqlMapClientCallback<Object>() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.insert(statementName, parameterObject);
			}
		}, this.getSqlMapClientMaster());
	}

	public int update(String statementName) throws DataAccessException {
		return update(statementName, null);
	}

	public int update(final String statementName, final Object parameterObject)
			throws DataAccessException {

		return execute(new SqlMapClientCallback<Integer>() {
			public Integer doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.update(statementName, parameterObject);
			}
		}, this.getSqlMapClientMaster());
	}

	public void update(String statementName, Object parameterObject, int requiredRowsAffected)
			throws DataAccessException {

		int actualRowsAffected = update(statementName, parameterObject);
		if (actualRowsAffected != requiredRowsAffected) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
					statementName, requiredRowsAffected, actualRowsAffected);
		}
	}

	public int delete(String statementName) throws DataAccessException {
		return delete(statementName, null);
	}

	public int delete(final String statementName, final Object parameterObject)
			throws DataAccessException {

		return execute(new SqlMapClientCallback<Integer>() {
			public Integer doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				return executor.delete(statementName, parameterObject);
			}
		}, this.getSqlMapClientMaster());
	}

	public void delete(String statementName, Object parameterObject, int requiredRowsAffected)
			throws DataAccessException {

		int actualRowsAffected = delete(statementName, parameterObject);
		if (actualRowsAffected != requiredRowsAffected) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
					statementName, requiredRowsAffected, actualRowsAffected);
		}
	}
	
	/**
	 * Execute the given data access action on a SqlMapExecutor.
	 * @param action callback object that specifies the data access action
	 * @return a result object returned by the action, or <code>null</code>
	 * @throws DataAccessException in case of SQL Maps errors
	 */
	public <T> T execute(SqlMapClientCallback<T> action) throws DataAccessException {
		return execute(action, this.getSqlMapClientMaster());
	}

	/**
	 * Execute the given data access action on a SqlMapExecutor.
	 * @param action callback object that specifies the data access action
	 * @return a result object returned by the action, or <code>null</code>
	 * @throws DataAccessException in case of SQL Maps errors
	 */
	public <T> T execute(SqlMapClientCallback<T> action, SqlMapClient sqlMapClient) throws DataAccessException {
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(sqlMapClient, "No SqlMapClient specified");

		// We always need to use a SqlMapSession, as we need to pass a Spring-managed
		// Connection (potentially transactional) in. This shouldn't be necessary if
		// we run against a TransactionAwareDataSourceProxy underneath, but unfortunately
		// we still need it to make iBATIS batch execution work properly: If iBATIS
		// doesn't recognize an existing transaction, it automatically executes the
		// batch for every single statement...

		SqlMapSession session = sqlMapClient.openSession();
		if (logger.isDebugEnabled()) {
			logger.debug("Opened SqlMapSession [" + session + "] for iBATIS operation");
		}
		Connection ibatisCon = null;

		try {
			Connection springCon = null;
			DataSource dataSource = sqlMapClient.getDataSource();
			boolean transactionAware = (dataSource instanceof TransactionAwareDataSourceProxy);

			// Obtain JDBC Connection to operate on...
			try {
				ibatisCon = session.getCurrentConnection();
				if (ibatisCon == null) {
					springCon = (transactionAware ?
							dataSource.getConnection() : DataSourceUtils.doGetConnection(dataSource));
					session.setUserConnection(springCon);
					if (logger.isDebugEnabled()) {
						logger.debug("Obtained JDBC Connection [" + springCon + "] for iBATIS operation");
					}
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Reusing JDBC Connection [" + ibatisCon + "] for iBATIS operation");
					}
				}
			}
			catch (SQLException ex) {
				throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
			}

			// Execute given callback...
			try {
				return action.doInSqlMapClient(session);
			}
			catch (SQLException ex) {
				throw getExceptionTranslator().translate("SqlMapClient operation", null, ex);
			}
			finally {
				try {
					if (springCon != null) {
						if (transactionAware) {
							springCon.close();
						}
						else {
							DataSourceUtils.doReleaseConnection(springCon, dataSource);
						}
					}
				}
				catch (Throwable ex) {
					logger.debug("Could not close JDBC Connection", ex);
				}
			}

			// Processing finished - potentially session still to be closed.
		}
		finally {
			// Only close SqlMapSession if we know we've actually opened it
			// at the present level.
			if (ibatisCon == null) {
				session.close();
			}
		}
	}


	/**
	 * If no DataSource specified, use SqlMapClient's DataSource.
	 * @see com.ibatis.sqlmap.client.SqlMapClient#getDataSource()
	 */
	@Override
	@Deprecated
	public DataSource getDataSource() {
		DataSource ds = super.getDataSource();
		return (ds != null ? ds : this.sqlMapClientMaster.getDataSource());
	}

	/**
	 * Set the JDBC DataSource to obtain connections from.
	 */
	@Override
	@Deprecated
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

}
