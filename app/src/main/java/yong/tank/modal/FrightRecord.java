package yong.tank.modal;

import java.io.Serializable;

public class FrightRecord  implements Serializable {
	private int id;
	private int withComputerTime;
	private int withBlueTime;
	private int withInternetTime;
	private int winComputerTime;
	private int winBlueTime;
	private int winInternetTime;
	public int getWithComputerTime() {
		return withComputerTime;
	}
	public void setWithComputerTime(int withComputerTime) {
		this.withComputerTime = withComputerTime;
	}
	public int getWithBlueTime() {
		return withBlueTime;
	}
	public void setWithBlueTime(int withBlueTime) {
		this.withBlueTime = withBlueTime;
	}
	public int getWithInternetTime() {
		return withInternetTime;
	}
	public void setWithInternetTime(int withInternetTime) {
		this.withInternetTime = withInternetTime;
	}
	public int getWinComputerTime() {
		return winComputerTime;
	}
	public void setWinComputerTime(int winComputerTime) {
		this.winComputerTime = winComputerTime;
	}
	public int getWinBlueTime() {
		return winBlueTime;
	}
	public void setWinBlueTime(int winBlueTime) {
		this.winBlueTime = winBlueTime;
	}
	public int getWinInternetTime() {
		return winInternetTime;
	}
	public void setWinInternetTime(int winInternetTime) {
		this.winInternetTime = winInternetTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "FrightRecord [id=" + id + ", withComputerTime=" + withComputerTime + ", withBlueTime=" + withBlueTime
				+ ", withInternetTime=" + withInternetTime + ", winComputerTime=" + winComputerTime + ", winBlueTime="
				+ winBlueTime + ", winInternetTime=" + winInternetTime + "]";
	}



}
