package com.pinyougou.pojogroup;

import java.io.Serializable;

public class Password implements Serializable {
	private String oldPassword;
	private String newPassword01;
	private String newPassword02;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword01() {
		return newPassword01;
	}

	public void setNewPassword01(String newPassword01) {
		this.newPassword01 = newPassword01;
	}

	public String getNewPassword02() {
		return newPassword02;
	}

	public void setNewPassword02(String newPassword02) {
		this.newPassword02 = newPassword02;
	}

	@Override
	public String toString() {
		return "Password [oldPassword=" + oldPassword + ", newPassword01=" + newPassword01 + ", newPassword02="
				+ newPassword02 + "]";
	}

}
