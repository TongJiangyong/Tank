package yong.tank.modal;

import java.io.Serializable;
import java.util.Date;

public class User  implements Serializable {
	private int id;
	private String username;
	private String password;
	private Date registerDate;
	private Date lastLoginDate;
	private FrightRecord frightRecord;
	private int state; //上线/ 未上线   0/1
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public FrightRecord getFrightRecord() {
		return frightRecord;
	}
	public void setFrightRecord(FrightRecord frightRecord) {
		this.frightRecord = frightRecord;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", registerDate=" + registerDate
				+ ", lastLoginDate=" + lastLoginDate + ", frightRecord=" + frightRecord.toString()+ "]";
	}




}
