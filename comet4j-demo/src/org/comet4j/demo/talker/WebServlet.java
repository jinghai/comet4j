/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;
import org.comet4j.core.util.JSONUtil;
import org.comet4j.demo.talker.dto.RenameDTO;
import org.comet4j.demo.talker.dto.TalkDTO;
import org.comet4j.demo.talker.dto.UserDTO;

/**
 * web交互
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-3
 */

public class WebServlet extends HttpServlet {

	/** serialVersionUID */
	private static final long serialVersionUID = -1311176251844328163L;
	private static final String CMD_FLAG = "cmd";
	private static final String RENAME_CMD = "rename";
	private static final String TALK_CMD = "talk";
	private static final String LIST_CMD = "list";
	private static final CometContext context = CometContext.getInstance();
	private static final CometEngine engine = context.getEngine();
	private static final AppStore appStore = AppStore.getInstance();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String cmd = request.getParameter(CMD_FLAG);
		// 改名
		if (RENAME_CMD.equals(cmd)) {
			String id = request.getParameter("id");
			if (id == null) return;
			String newName = request.getParameter("newName");
			String oldName = request.getParameter("oldName");
			appStore.put(id, newName);
			RenameDTO dto = new RenameDTO(id, oldName, newName);
			engine.sendToAll(Constant.APP_CHANNEL, dto);
			return;
		}
		// 发送信息
		if (TALK_CMD.equals(cmd)) {
			String id = request.getParameter("id");
			String name = appStore.get(id);
			String text = request.getParameter("text");
			TalkDTO dto = new TalkDTO(id, name, text);
			engine.sendToAll(Constant.APP_CHANNEL, dto);
			return;
		}
		// 在线列表
		if (LIST_CMD.equals(cmd)) {
			List<UserDTO> userList = new ArrayList<UserDTO>();
			Map<String, String> map = AppStore.getInstance().getMap();
			Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = iter.next();
				String id = entry.getKey();
				String name = entry.getValue();
				userList.add(new UserDTO(id, name));
			}
			String json = JSONUtil.object2json(userList);
			response.getWriter().print(json);
		}
	}
}
