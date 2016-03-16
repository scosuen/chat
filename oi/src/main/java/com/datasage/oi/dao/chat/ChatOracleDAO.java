package com.datasage.oi.dao.chat;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.datasage.oi.common.utils.DBTemplate;
import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.config.datasource.DataSourceFactory;
import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;
import com.datasage.oi.model.chat.ChatSessionUsers;

@Repository
public class ChatOracleDAO implements IChatDAO {

	private static Logger logger = LoggerFactory.getLogger(ChatOracleDAO.class);

	private static DataSource dataSource;

	@Resource(name = "dataSourceFactory")
	private void DBTemplate(DataSourceFactory dataSourceFactory) {
		dataSource = dataSourceFactory.getDataSource();
	}

	@Resource
	private DBTemplate dbTemplate;

	@Override
	public Long insertChatSession(ChatSession chatSession) {
		Connection conn = null;
		CallableStatement cst = null;

		try {
			conn = dataSource.getConnection();
			cst = conn
					.prepareCall("{ call pkg_cc.pro_insert_session(:create_user_id, :create_user_name, :create_time, :session_id)}");

			cst.setLong("create_user_id", chatSession.getCreateUserId());
			cst.setString("create_user_name", ObjectTransition.strOracleTransMeaning(chatSession.getCreateUserName()));
			cst.setTimestamp("create_time", ObjectTransition.date2Timestamp(chatSession.getCreateTime()));

			cst.registerOutParameter("session_id", Types.NUMERIC);

			cst.execute();

			return cst.getLong("session_id");

		} catch (Exception e) {
			e.printStackTrace();
			return 0l;
		} finally {
			try {
				dbTemplate.close(cst, conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int insertUser(ChatSessionUsers chatSessionUsers) {
		return dbTemplate.executeUpdate(buildChatSessionUsersInsertSQL(chatSessionUsers).toString());
	}
	
	private String buildChatSessionUsersInsertSQL (ChatSessionUsers chatSessionUsers) {
		StringBuffer sql = new StringBuffer("insert into cc_session_users(session_users_id, session_id, user_id, join_time, user_name) ");
		sql.append("values (cc_session_users_seq.nextval, ");
		sql.append(chatSessionUsers.getSessionId()).append(", ");
		sql.append(chatSessionUsers.getUserId()).append(", ");
		sql.append("to_date('").append(ObjectTransition.dateToSting(chatSessionUsers.getJoinTime())).append("','yyyy-mm-dd hh24:mi:ss'), ");
		sql.append("'").append(ObjectTransition.strOracleTransMeaning(chatSessionUsers.getUserName())).append("'");
		sql.append(")");
		
		return sql.toString();
	}
	
	private String buildChatSessionUsersDeleteSQL (ChatSessionUsers chatSessionUsers) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete cc_session_users cus where cus.user_id = ");
		sql.append(chatSessionUsers.getUserId());
		sql.append(" and cus.user_name = '").append(ObjectTransition.strOracleTransMeaning(chatSessionUsers.getUserName())).append("'");
		
		return sql.toString();
	}

	@Override
	public ChatSession querySessionById(Long sessionId) {
		StringBuffer sql = new StringBuffer("select session_id, create_time, create_user_id, create_user_name, update_time from cc_session where session_id = ");
		sql.append(sessionId);

		List<Map<String, Object>> result = dbTemplate.executeQuery(sql.toString());

		if (result == null || result.isEmpty())
			return null;

		ChatSession chatSession = new ChatSession();
		chatSession.setSessionId(ObjectTransition.object2Long(result.get(0).get("SESSION_ID")));
		chatSession.setCreateUserId(ObjectTransition.object2Long(result.get(0).get("CREATE_USER_ID")));
		chatSession.setCreateTime((java.sql.Timestamp) result.get(0).get("CREATE_TIME"));
		chatSession.setCreateUserName(ObjectTransition.object2String(result.get(0).get("CREATE_USER_NAME")));
		chatSession.setCreateTime((java.sql.Timestamp) result.get(0).get("UPDATE_TIME"));
		return chatSession;
	}

	@Override
	public int insertMessage(ChatSessionMessages chatSessionMessage) {
		return dbTemplate.executeUpdate(buildInsertMessageSql(chatSessionMessage));
	}

	private String buildInsertMessageSql (ChatSessionMessages chatSessionMessage) {
		StringBuffer sql = new StringBuffer(
				"insert into cc_session_messages (session_messages_id, session_id, send_user_id, message, send_time, send_user_name) ");
		sql.append("values (cc_session_messages_seq.nextval, ").append(chatSessionMessage.getSessionId());
		sql.append(", ").append(chatSessionMessage.getSendUserId());
		sql.append(", '").append(ObjectTransition.strOracleTransMeaning(chatSessionMessage.getMessage())).append("'");
		sql.append(", to_date('").append(ObjectTransition.dateToSting(chatSessionMessage.getSendTime())).append("','yyyy-mm-dd hh24:mi:ss')");
		sql.append(", '").append(ObjectTransition.strOracleTransMeaning(chatSessionMessage.getSendUserName())).append("'");
		sql.append(")");
		
		return sql.toString();
	}
	
	@Override
	public int[] insertChatSessionUserList(List<ChatSessionUsers> chatSessionUsersList) {
		if (chatSessionUsersList == null || chatSessionUsersList.isEmpty())
			return new int[0];
		
		List<String> sqls = new ArrayList<String>();
		
		for (ChatSessionUsers chatSessionUsers : chatSessionUsersList) {
			sqls.add(buildChatSessionUsersInsertSQL(chatSessionUsers));
		}
		
		return dbTemplate.executeUpdateBatch(sqls);
	}

	@Override
	public int[] deleteChatSessionUserList(List<ChatSessionUsers> chatSessionUsersList) {
		if (chatSessionUsersList == null || chatSessionUsersList.isEmpty())
			return new int[0];
		
		List<String> sqls = new ArrayList<String>();
		
		for (ChatSessionUsers chatSessionUsers : chatSessionUsersList) {
			sqls.add(buildChatSessionUsersDeleteSQL(chatSessionUsers));
		}
		
		return dbTemplate.executeUpdateBatch(sqls);
	}

	@Override
	public List<ChatSessionUsers> querySessionUserListBySessionId(Long sessionId) {
		StringBuffer sql = new StringBuffer("select session_users_id, session_id, user_id, join_time, user_name from cc_session_users where session_id = ");
		sql.append(sessionId);
		sql.append(" order by join_time");
		
		List<Map<String, Object>> result = dbTemplate.executeQuery(sql.toString());
		if (result == null || result.isEmpty())
			return new ArrayList<ChatSessionUsers>();
		
		List<ChatSessionUsers> chatSessionUserList = new ArrayList<ChatSessionUsers>();
		ChatSessionUsers chatSessionUser = null;
		for (Map<String, Object> row : result) {
			chatSessionUser = new ChatSessionUsers();
			chatSessionUser.setSessionUserId(ObjectTransition.object2Long(row.get("SESSION_USERS_ID")));
			chatSessionUser.setSessionId(ObjectTransition.object2Long(row.get("SESSION_ID")));
			chatSessionUser.setUserId(ObjectTransition.object2Long(row.get("USER_ID")));
			chatSessionUser.setUserName(ObjectTransition.object2String(row.get("USER_NAME")));
			chatSessionUser.setJoinTime((java.sql.Timestamp)row.get("JOIN_TIME"));
			
			chatSessionUserList.add(chatSessionUser);
		}
		
		return chatSessionUserList;
	}

	@Override
	public List<ChatSessionMessages> querySessionMessageList(ChatSessionMessages chatSessionMessages) {
		StringBuffer sql = new StringBuffer("select session_messages_id, session_id, send_user_id, message, send_time, send_user_name from cc_session_messages csm ");
		sql.append(" where csm.session_id = ").append(chatSessionMessages.getSessionId());
		sql.append(" and csm.send_time >= to_date('").append(ObjectTransition.dateToSting(chatSessionMessages.getSendTime())).append("', 'yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" and csm.send_user_id <> ").append(chatSessionMessages.getSendUserId());
		sql.append(" order by csm.send_time");
		
		List<Map<String, Object>> result = dbTemplate.executeQuery(sql.toString());
		if (result == null || result.isEmpty())
			return new ArrayList<ChatSessionMessages>();
		
		List<ChatSessionMessages> messageList = new ArrayList<ChatSessionMessages>();
		ChatSessionMessages message = null;
		for (Map<String, Object> row : result) {
			message = new ChatSessionMessages();
			
			message.setSessionMessagesId(ObjectTransition.object2Long(row.get("SESSION_MESSAGES_ID")));
			message.setSessionId(ObjectTransition.object2Long(row.get("SESSION_ID")));
			message.setSendUserId(ObjectTransition.object2Long(row.get("SEND_USER_ID")));
			message.setSendUserName(ObjectTransition.object2String(row.get("SEND_USER_NAME")));
			message.setSendTime((java.sql.Timestamp)row.get("SEND_TIME"));
			message.setMessage(ObjectTransition.object2String(row.get("MESSAGE")));
			
			messageList.add(message);
		}
		
		return messageList;
	}

	@Override
	public List<ChatSession> querySessionsByUserId(ChatSession chatSession) {
		StringBuffer sql = new StringBuffer(" select ccs.session_id, ccs.create_time, ccs.create_user_id, ccs.create_user_name, ccs.update_time from cc_session ccs ");
		sql.append(" where ccs.session_id in (select csu.session_id from cc_session_users csu where csu.user_id = ").append(chatSession.getCreateUserId()).append(") ");
		sql.append(" and update_time > ").append("to_date('").append(ObjectTransition.dateToSting(chatSession.getUpdateTime())).append("','yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" order by ccs.update_time ");
		
		return buildChatSessionModel(dbTemplate.executeQuery(sql.toString()));
	}
	
	@Override
	public List<ChatSession> querySessions(ChatSession chatSession) {
		StringBuffer sql = new StringBuffer("select ccs.session_id, ccs.create_time, ccs.create_user_id, ccs.create_user_name, ccs.update_time from cc_session ccs ");
		sql.append(" where 1 = 1 ");
		sql.append(" and ccs.update_time > ").append("to_date('").append(ObjectTransition.dateToSting(chatSession.getUpdateTime())).append("','yyyy-mm-dd hh24:mi:ss') ");
		
		return buildChatSessionModel(dbTemplate.executeQuery(sql.toString()));
	}
	
	private List<ChatSession> buildChatSessionModel (List<Map<String, Object>> list) {
		List<ChatSession> chatSessionList = new ArrayList<ChatSession>();
		
		if (list == null || list.isEmpty())
			return chatSessionList;
		
		ChatSession chatSessionResult = null;
		for (Map<String, Object> row : list) {
			chatSessionResult = new ChatSession();
			chatSessionResult.setSessionId(ObjectTransition.object2Long(row.get("SESSION_ID")));
			chatSessionResult.setCreateUserId(ObjectTransition.object2Long(row.get("CREATE_USER_ID")));
			chatSessionResult.setCreateUserName(ObjectTransition.object2String(row.get("CREATE_USER_NAME")));
			chatSessionResult.setCreateTime((java.sql.Timestamp)row.get("CREATE_TIME"));
			chatSessionResult.setUpdateTime((java.sql.Timestamp)row.get("UPDATE_TIME"));
			
			chatSessionList.add(chatSessionResult);
		}
		
		return chatSessionList;
	}

	@Override
	public List<ChatSessionUsers> queryAllUsers() {
		StringBuffer sql = new StringBuffer(" select target_id, user_id, oi_target.get_short_name(target_id) short_name ");
		sql.append(" from oi_targets ");
		sql.append("  where user_id is not null ");
		
		List<Map<String, Object>> result = dbTemplate.executeQuery(sql.toString());
		if (result == null || result.isEmpty())
			return new ArrayList<ChatSessionUsers>();
		
		List<ChatSessionUsers> chatSessionUserList = new ArrayList<ChatSessionUsers>();
		ChatSessionUsers chatSessionUser = null;
		
		for (Map<String, Object> row : result) {
			chatSessionUser = new ChatSessionUsers();
			chatSessionUser.setUserId(ObjectTransition.object2Long(row.get("TARGET_ID")));
			chatSessionUser.setUserName(ObjectTransition.object2String(row.get("SHORT_NAME")));
			
			chatSessionUserList.add(chatSessionUser);
		}
		
		return chatSessionUserList;
	}

	@Override
	public List<ChatSessionUsers> queryAllUsersBySessionId(long sessionId) {
		StringBuffer sql = new StringBuffer(" select ot.target_id, oi_target.get_short_name(target_id) short_name from oi_targets ot where user_id is not null ");
		sql.append(" minus ");
		sql.append(" select csu.user_id, csu.user_name from cc_session_users csu where csu.session_id = ").append(sessionId);
		
		
		List<Map<String, Object>> result = dbTemplate.executeQuery(sql.toString());
		if (result == null || result.isEmpty())
			return new ArrayList<ChatSessionUsers>();
		
		List<ChatSessionUsers> chatSessionUserList = new ArrayList<ChatSessionUsers>();
		ChatSessionUsers chatSessionUser = null;
		
		for (Map<String, Object> row : result) {
			chatSessionUser = new ChatSessionUsers();
			chatSessionUser.setUserId(ObjectTransition.object2Long(row.get("TARGET_ID")));
			chatSessionUser.setUserName(ObjectTransition.object2String(row.get("SHORT_NAME")));
			
			chatSessionUserList.add(chatSessionUser);
		}
		
		return chatSessionUserList;
	}

	@Override
	public void updateSessionUpdateTime() {
		Connection conn = null;
		CallableStatement cst = null;

		try {
			conn = dataSource.getConnection();
			cst = conn.prepareCall("{ call pkg_cc.pro_upd_session_updtime}");

			cst.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbTemplate.close(cst, conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int[] insertMessageList(List<ChatSessionMessages> chatSessionMessageList) {
		if (chatSessionMessageList == null || chatSessionMessageList.isEmpty())
			return new int[0];
		
		List<String> sqls = new ArrayList<String>();
		
		for (ChatSessionMessages chatSessionMessage : chatSessionMessageList) {
			sqls.add(buildInsertMessageSql(chatSessionMessage));
		}
		
		return dbTemplate.executeUpdateBatch(sqls);
	}

	
}
