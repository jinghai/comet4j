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
 * 改名通知传输对象
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-3
 */
public class RenameDTO {

	private final String transtime;
	private String type;
	private String id;
	private String newName;
	private String oldName;

	public RenameDTO(String id, String oldName, String newName) {
		this.type = Constant.RENAME;
		this.id = id;
		this.oldName = oldName;
		this.newName = newName;
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		transtime = f.format(d);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getTranstime() {
		return transtime;
	}
}
