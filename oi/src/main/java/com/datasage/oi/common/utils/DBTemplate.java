package com.datasage.oi.common.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.datasage.oi.config.datasource.DataSourceFactory;

@Component
public class DBTemplate {

	private static DataSource dataSource;

	@Resource(name = "dataSourceFactory")
	private void DBTemplate(DataSourceFactory dataSourceFactory) {
		dataSource = dataSourceFactory.getDataSource();
	}

	/**
	 * 查询 , 返回List, 不关闭Connection! 返回key列名大写
	 * 
	 * @param sql
	 * @return
	 * @throws GenericException
	 */
	public List executeQueryNotClose(String sql, Connection conn) {
		ResultSet rs = null;
		PreparedStatement pst = null;

		Map resultMap = null;
		List resultList = new ArrayList();

		try {
			if (conn == null)
				conn = dataSource.getConnection();

			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				resultMap = new HashMap();
				// 取列名做key
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					resultMap.put(rs.getMetaData().getColumnName(i),
							rs.getObject(i));
				}
				resultList.add(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				close(rs, pst, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return resultList;
	}

	/**
	 * 查询, 返回List, 返回key列名大写
	 * 
	 * @param sql
	 * @return
	 * @throws GenericException
	 */
	public List<Map<String, Object>> executeQuery(String sql) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;

		Map<String, Object> resultMap = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		try {
			conn = dataSource.getConnection();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				resultMap = new HashMap<String, Object>();
				// 取列名做key
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					resultMap.put(rs.getMetaData().getColumnName(i),
							rs.getObject(i));
				}
				resultList.add(resultMap);
			}
			return resultList;

		} catch (Exception e) {
			e.printStackTrace();
			return resultList;
		} finally {
			try {
				close(rs, pst, conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新 insert或update
	 * 
	 * @param sql
	 * @return
	 * @throws GenericException
	 */
	public int executeUpdate(String sql) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			pst = conn.prepareStatement(sql);
			return pst.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				close(rs, pst, conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新 insert或update, 不关闭 Connection!
	 * 
	 * @param sql
	 * @param conn
	 * @return
	 * @throws GenericException
	 */
	public int executeUpdateNotClose(String sql, Connection conn) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			if (conn == null)
				conn = dataSource.getConnection();
			pst = conn.prepareStatement(sql);
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				close(rs, pst, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 批次执行 不关闭Connection
	 * 
	 * @param list
	 * @param conn
	 * @return
	 * @throws GenericException
	 */
	public int[] executeUpdateBatchNotClose(List list, Connection conn) {
		if (list == null || list.size() <= 0)
			return new int[0];

		Statement stmt = null;
		String sql = null;
		try {
			if (conn == null)
				conn = dataSource.getConnection();
			stmt = conn.createStatement();

			for (int i = 0; i < list.size(); i++) {
				sql = null;
				sql = (String) list.get(i);

				if (sql != null && !"".equals(sql))
					stmt.addBatch(sql);
			}
			return stmt.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
			return new int[0];
		} finally {
			try {
				close(null, stmt, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * batch update
	 * 
	 * @param list
	 * @return
	 * @throws GenericException
	 */
	public int[] executeUpdateBatch(List<String> list) {
		if (list == null || list.size() <= 0)
			return new int[0];

		String sql = null;
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();

			for (int i = 0; i < list.size(); i++) {
				sql = null;
				sql = list.get(i);

				if (sql != null && !"".equals(sql))
					stmt.addBatch(sql);
			}
			return stmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return new int[0];
		} finally {
			try {
				close(null, stmt, conn);
			} catch (SQLException e) {
				e.printStackTrace();
				return new int[0];
			}
		}

	}

	public void close(ResultSet rset, Statement stmt, Connection conn)
			throws SQLException {
		if (rset != null)
			rset.close();
		if (stmt != null)
			stmt.close();
		if (conn != null)
			conn.close();
	}

	public void close(CallableStatement callableStatement, Connection conn) throws SQLException {
		if (callableStatement != null)
			callableStatement.close();
		if (conn != null)
			conn.close();
	}
}
