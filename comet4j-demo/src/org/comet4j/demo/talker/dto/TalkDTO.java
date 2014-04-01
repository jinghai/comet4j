/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.comet4j.demo.talker.Constant;

/**
 * 聊天信息传输对象
 * @author jinghai.xiao@gmail.com
 * @date 2011-4-7
 */

public class TalkDTO {

	private final String transtime;
	private String type;
	private String id;
	private String name;
	private String text;

	public TalkDTO(String id, String name, String text) {
		this.type = Constant.TALK;
		this.id = id;
		this.name = name;
		this.text = text;
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		transtime = f.format(d);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTranstime() {
		return transtime;
	}
}
