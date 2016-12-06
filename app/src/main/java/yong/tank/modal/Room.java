package yong.tank.modal;

import java.io.Serializable;
import java.util.Date;

public class Room  implements Serializable {
	private int id;
	private int serverUser;
	private int clientUser;
	private int state; //1有一个人--等待状态  2 有两个人--进行状态  0 没有人--退出状态
	private Date creatTime;
	private String roomName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	public int getServerUser() {
		return serverUser;
	}
	public void setServerUser(int serverUser) {
		this.serverUser = serverUser;
	}
	public int getClientUser() {
		return clientUser;
	}
	public void setClientUser(int clientUser) {
		this.clientUser = clientUser;
	}

	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	@Override
	public String toString() {
		return "Room [id=" + id + ", serverUser=" + serverUser + ", clientUser=" + clientUser + ", state=" + state
				+ ", creatTime=" + creatTime + ", roomName=" + roomName + "]";
	}


}
